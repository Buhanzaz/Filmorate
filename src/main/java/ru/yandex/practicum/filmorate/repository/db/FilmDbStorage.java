package ru.yandex.practicum.filmorate.repository.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilm() {
        String sqlQuery = "SELECT * FROM FILMS "
                + "JOIN MPA_RATING ON FILMS.MPA_RATING_ID = MPA_RATING.RATING_ID ";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);

        return addDirectorsInFilm(addGenreInFilms(addLikesInFilms(films)));
    }

    @Override
    public Film create(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("name", "description", "duration", "release_date", "mpa_rating_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of(
                        "name", film.getName(),
                        "description", film.getDescription(),
                        "duration", film.getDuration(),
                        "release_date", java.sql.Date.valueOf(film.getReleaseDate()),
                        "mpa_rating_id", film.getMpa().getId()))
                .getKeys();

        if (keys != null) {
            film.setId((Integer) keys.get("film_id"));
            addGenre((Integer) keys.get("film_id"), film.getGenres());
            addDirector((Integer) keys.get("film_id"), film.getDirectors());
            film.setGenres(getGenres(film.getId()));
            film.setDirectors(getDirectors(film.getId()));
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        String sqlQuery = "UPDATE FILMS "
                + "SET NAME = ?, "
                + "DESCRIPTION = ?, "
                + "DURATION = ?, "
                + "RELEASE_DATE = ?, "
                + "MPA_RATING_ID = ? "
                + "WHERE FILM_ID = ?";

        getById(film.getId());
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId(), film.getId());
        addGenre(film.getId(), film.getGenres());
        addDirector(film.getId(), film.getDirectors());
        film.setGenres(getGenres(filmId));
        film.setDirectors(getDirectors(filmId));

        return getById(filmId);
    }

    @Override
    public void delete(int filmId) {
        jdbcTemplate.update("DELETE FROM FILMS WHERE FILM_ID = ?", filmId);
    }

    @Override
    public Film getById(Integer filmId) {
        String sqlQuery = "SELECT * FROM FILMS "
                + "JOIN MPA_RATING ON FILMS.MPA_RATING_ID = MPA_RATING.RATING_ID "
                + "WHERE FILM_ID = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (srs.next()) {
            Film film = filmMap(srs);

            getLikes(filmId).forEach(film::addLike);

            return film;
        } else {
            throw new NotFoundException("Movie with ID = " + filmId + " not found!");
        }
    }

    public void addGenre(int filmId, Set<Genre> genres) {
        deleteAllGenresById(filmId);
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sqlQuery = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) "
                + "VALUES (?, ?)";
        List<Genre> genresTable = new ArrayList<>(genres);
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresTable.get(i).getId());
            }

            public int getBatchSize() {
                return genresTable.size();
            }
        });
    }

    public void addDirector(int filmId, Set<Director> directors) {
        deleteAllDirectorsById(filmId);
        if (directors == null || directors.isEmpty()) {
            return;
        }
        String sqlQuery = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) "
                + "VALUES (?, ?)";
        List<Director> directorList = new ArrayList<>(directors);
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, directorList.get(i).getId());
            }

            public int getBatchSize() {
                return directorList.size();
            }
        });
    }

    private SortedSet<Genre> getGenres(int filmId) {
        String sqlQuery = "SELECT FILM_GENRES.GENRE_ID, GENRES.GENRE FROM FILM_GENRES "
                + "JOIN GENRES ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID "
                + "WHERE FILM_ID = ? ORDER BY GENRE_ID";
        return new TreeSet<>(jdbcTemplate.query(sqlQuery, this::makeGenre, filmId));
    }

    private List<Integer> getLikes(int filmId) {
        List<FilmLikes> filmsLikes =
                jdbcTemplate.query(
                        "select film_id, user_id from likes where film_id = ?",
                        (rs, rowNum) -> new FilmLikes(rs.getInt(1), rs.getInt(2)), filmId);

        return filmsLikes.stream().map(FilmLikes::getUserId).collect(Collectors.toList());
    }

    private void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    private void deleteAllDirectorsById(int filmId) {
        String sglQuery = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    private Set<Director> getDirectors(int filmId) {
        Comparator<Director> compId = Comparator.comparing(Director::getId);
        Set<Director> directors = new TreeSet<>(compId);
        String sqlQuery = "SELECT D.DIRECTOR_ID, D.NAME FROM DIRECTORS AS D "
                + "JOIN FILM_DIRECTORS AS FD ON D.DIRECTOR_ID = FD.DIRECTOR_ID "
                + "WHERE FILM_ID = ?";
        directors.addAll(jdbcTemplate.query(sqlQuery, this::makeDirector, filmId));
        return directors;
    }

    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (FILM_ID, user_id) "
                + "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(Integer directorId) {
        String sqlQuery = "WITH DIRECTOR_FILMS AS " +
                "(SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, " +
                "F.MPA_RATING_ID, MPA.RATING " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATING AS MPA ON F.MPA_RATING_ID = MPA.RATING_ID " +
                "JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "WHERE FD.DIRECTOR_ID = ?)\n" +

                "SELECT DF.FILM_ID, DF.NAME, DF.DESCRIPTION, DF.RELEASE_DATE, DF.DURATION, " +
                "DF.MPA_RATING_ID, DF.RATING FROM DIRECTOR_FILMS AS DF " +
                "LEFT JOIN LIKES AS L ON L.FILM_ID = DF.FILM_ID " +
                "GROUP BY DF.FILM_ID " +
                "ORDER BY COUNT (L.FILM_ID) DESC";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
        return addDirectorsInFilm(addLikesInFilms(addGenreInFilms(films)));
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(int directorId) {
        String sql = "SELECT * FROM FILMS AS F "
                + "JOIN MPA_RATING AS MPA ON F.MPA_RATING_ID = MPA.RATING_ID "
                + "JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID "
                + "WHERE FD.DIRECTOR_ID = ? "
                + "ORDER BY EXTRACT(YEAR FROM release_date)";

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, directorId);
        return addDirectorsInFilm(addLikesInFilms(addGenreInFilms(films)));
    }

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        String sql = "SELECT f.*, m.rating " +
                "FROM films AS f " +
                "JOIN mpa_rating AS m ON m.rating_id = f.mpa_rating_id " +
                "WHERE f.film_id IN " +
                "(SELECT DISTINCT l.film_id " +
                "FROM likes AS l " +
                "WHERE l.user_id IN " +
                "(SELECT u.user_id " +
                "FROM (SELECT user_id, COUNT(*) AS matches " +
                "FROM likes " +
                "WHERE NOT user_id = ? " +
                "AND film_id IN " +
                "(SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ?) " +
                "GROUP BY user_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 1) AS u) " +
                "AND l.film_id NOT IN " +
                "(SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ?))";

        List<Film> filmList = jdbcTemplate.query(sql, this::makeFilm, userId, userId, userId);
        return addDirectorsInFilm(addLikesInFilms(addGenreInFilms(filmList)));
    }

    public List<Film> getFilmsByUserId(int userId) {
        String sqlQuery = "SELECT * FROM FILMS F" +
                " JOIN MPA_RATING MR ON F.MPA_RATING_ID = MR.RATING_ID" +
                " LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID" +
                " WHERE USER_ID = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, userId);
        return addDirectorsInFilm(addLikesInFilms(addGenreInFilms(films)));
    }

    private List<Film> addDirectorsInFilm(List<Film> films) {
        String sql = "SELECT FD.FILM_ID, FD.DIRECTOR_ID, D.NAME " +
                "FROM FILM_DIRECTORS AS FD " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID";
        List<FilmDirector> filmDirectors = jdbcTemplate.query(sql,
                (rs, rowNum) -> new FilmDirector(rs.getInt(1), rs.getInt(2),
                        rs.getString(3)));

        films.forEach(film -> filmDirectors.stream()
                .filter(filmDirector -> film.getId() == filmDirector.filmId)
                .forEach(filmDirector -> film.getDirectors().add(new Director(filmDirector.directorId,
                        filmDirector.directorName))));

        return films;
    }

    private List<Film> addLikesInFilms(List<Film> films) {
        List<FilmLikes> filmsLikes =
                jdbcTemplate.query(
                        "select film_id, user_id from likes",
                        (rs, rowNum) -> new FilmLikes(rs.getInt(1), rs.getInt(2)));
        films.forEach(film -> filmsLikes.stream()
                .filter(filmsLike -> film.getId() == filmsLike.filmId)
                .forEach(filmsLike -> film.addLike(filmsLike.userId)));
        return films;
    }

    private List<Film> addGenreInFilms(List<Film> films) {
        List<FilmGenre> filmGenres =
                jdbcTemplate.query(
                        "select FILM_GENRES.FILM_ID, GENRES.GENRE_ID, GENRES.GENRE "
                                + "from FILMS, FILM_GENRES, GENRES "
                                + "where FILMS.FILM_ID = FILM_GENRES.FILM_ID "
                                + "and FILM_GENRES.GENRE_ID = GENRES.GENRE_ID",
                        (rs, rowNum) -> new FilmGenre(rs.getInt(1), rs.getInt(2),
                                rs.getString(3)));

        films.forEach(film -> filmGenres.stream()
                .filter(filmGenre -> film.getId() == filmGenre.filmId)
                .forEach(filmGenre -> film.addGenre(new Genre(filmGenre.genreId, filmGenre.genreName))));

        return films;
    }

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre");
        return new Genre(genreId, genreName);
    }

    private Director makeDirector(ResultSet rs, int id) throws SQLException {
        int directorId = rs.getInt("director_id");
        String name = rs.getString("name");
        return Director.builder().id(directorId).name(name).build();
    }

    private Film makeFilm(ResultSet rs, int id) throws SQLException {
        int filmId = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
        int mpaId = rs.getInt("mpa_rating_id");
        String mpaName = rs.getString("rating");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);

        return Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .build();
    }

    private Film filmMap(SqlRowSet srs) {
        int id = srs.getInt("film_id");
        String name = srs.getString("name");
        String description = srs.getString("description");
        int duration = srs.getInt("duration");
        LocalDate releaseDate = Objects.requireNonNull(srs.getTimestamp("release_date"))
                .toLocalDateTime().toLocalDate();
        int mpaId = srs.getInt("rating_id");
        String mpaName = srs.getString("rating");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        SortedSet<Genre> genres = getGenres(id);

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .directors(getDirectors(id))
                .releaseDate(releaseDate)
                .build();
    }

    @AllArgsConstructor
    private static class FilmGenre {
        int filmId;
        int genreId;
        String genreName;
    }

    @Getter
    @AllArgsConstructor
    private static class FilmLikes {
        int filmId;
        int userId;
    }

    @Getter
    @AllArgsConstructor
    private static class FilmDirector {
        int filmId;
        int directorId;
        String directorName;
    }
}
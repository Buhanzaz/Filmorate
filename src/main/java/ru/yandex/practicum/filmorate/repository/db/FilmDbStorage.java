package ru.yandex.practicum.filmorate.repository.db;

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
                + "JOIN MPA_RATING ON FILMS.MPA_RATING_ID = MPA_RATING.RATING_ID";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilm(rs));
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
        getById(film.getId());
        String sqlQuery = "UPDATE FILMS "
                + "SET NAME = ?, "
                + "DESCRIPTION = ?, "
                + "DURATION = ?, "
                + "RELEASE_DATE = ?, "
                + "MPA_RATING_ID = ? "
                + "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId(), film.getId());
        addGenre(film.getId(), film.getGenres());
        addDirector(film.getId(), film.getDirectors());
        int filmId = film.getId();
        film.setGenres(getGenres(filmId));
        film.setDirectors(getDirectors(filmId));
        return getById(filmId);
    }

    @Override
    public String delete(int filmId) {
        return "DELETE FROM FILMS WHERE FILM_ID = ?";
    }

    @Override
    public Film getById(Integer filmId) {
        String sqlQuery = "SELECT * FROM FILMS "
                + "JOIN MPA_RATING ON FILMS.MPA_RATING_ID = MPA_RATING.RATING_ID "
                + "WHERE FILM_ID = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (srs.next()) {
            return filmMap(srs);
        } else {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
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

    private Set<Genre> getGenres(int filmId) {
        Comparator<Genre> compId = Comparator.comparing(Genre::getId);
        Set<Genre> genres = new TreeSet<>(compId);
        String sqlQuery = "SELECT FILM_GENRES.GENRE_ID, GENRES.GENRE FROM FILM_GENRES "
                + "JOIN GENRES ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID "
                + "WHERE FILM_ID = ? ORDER BY GENRE_ID ASC";
        genres.addAll(jdbcTemplate.query(sqlQuery, this::makeGenre, filmId));
        return genres;
    }

    private Set<Director> getDirectors(int filmId) {
        Comparator<Director> compId = Comparator.comparing(Director::getId);
        Set<Director> genres = new TreeSet<>(compId);
        String sqlQuery = "SELECT D.DIRECTOR_ID, D.NAME FROM DIRECTORS AS D "
                + "JOIN FILM_DIRECTORS AS FD ON D.DIRECTOR_ID = FD.DIRECTOR_ID "
                + "WHERE FILM_ID = ?";
        genres.addAll(jdbcTemplate.query(sqlQuery, this::makeDirector, filmId));
        return genres;
    }

    private void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    private void deleteAllDirectorsById(int filmId) {
        String sglQuery = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (FILM_ID, user_id) "
                + "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE likes "
                + "WHERE FILM_ID = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public List<Film> getTopFilm(Integer count) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, MPA.RATING_ID, " +
                "MPA.rating " +
                "FROM FILMS AS F "
                + "JOIN MPA_RATING AS MPA ON F.MPA_RATING_ID = MPA.RATING_ID "
                + "LEFT JOIN LIKES AS L ON L.FILM_ID = F.FILM_ID "
                + "GROUP BY F.FILM_ID "
                + "ORDER BY COUNT (L.FILM_ID) DESC "
                + "LIMIT "
                + count;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilm(rs));
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(int directorId) {
        String sqlQuery = "SELECT * FROM FILMS AS F "
                + "JOIN MPA_RATING AS MPA ON F.MPA_RATING_ID = MPA.RATING_ID "
                + "JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID "
                + "WHERE FD.DIRECTOR_ID = ? "
                + "ORDER BY EXTRACT(YEAR FROM release_date)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilm(rs), directorId);
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(Integer directorId) {

        String sqlQuery = "WITH DIRECTOR_FILMS AS " +
                "(SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, " +
                "MPA.RATING_ID, MPA.RATING " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATING AS MPA ON F.MPA_RATING_ID = MPA.RATING_ID " +
                "JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "WHERE FD.DIRECTOR_ID = ?)\n" +

                "SELECT DF.FILM_ID, DF.NAME, DF.DESCRIPTION, DF.RELEASE_DATE, DF.DURATION, " +
                "DF.RATING_ID, DF.RATING FROM DIRECTOR_FILMS AS DF " +
                "LEFT JOIN LIKES AS L ON L.FILM_ID = DF.FILM_ID " +
                "GROUP BY DF.FILM_ID " +
                "ORDER BY COUNT (L.FILM_ID) DESC";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilm(rs), directorId);
    }


    private List<Film> addGenre(List<Film> films) {
        Map<Integer, Film> filmsTable = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        String inSql = String.join(", ", Collections.nCopies(filmsTable.size(), "?"));
        final String sqlQuery = "SELECT * "
                + "FROM FILM_GENRES "
                + "LEFT OUTER JOIN genres ON FILM_GENRES.GENRE_ID = genres.GENRE_ID "
                + "WHERE FILM_GENRES.FILM_ID IN (" + inSql + ") "
                + "ORDER BY FILM_GENRES.GENRE_ID";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            filmsTable.get(rs.getInt("film_id")).addGenre(new Genre(rs.getInt("genre_id"),
                    rs.getString("genre")));
        }, filmsTable.keySet().toArray());
        return films;
    }

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre");
        return new Genre(genreId, genreName);
    }

    private Director makeDirector(ResultSet rs, int id) throws SQLException {
        Integer directorId = rs.getInt("director_id");
        String name = rs.getString("name");
        return new Director(directorId, name);
    }

    private Film makeFilm(ResultSet rs, int id) throws SQLException {
        int filmId = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
        int mpaId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = new HashSet<>();
        return Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
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
        Set<Genre> genres = getGenres(id);
        Set<Director> directors = getDirectors(id);
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .directors(directors)
                .releaseDate(releaseDate)
                .build();
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = Objects.requireNonNull(rs.getTimestamp("release_date"))
                .toLocalDateTime().toLocalDate();
        int mpaId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = getGenres(id);
        Set<Director> directors = getDirectors(id);
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .directors(directors)
                .releaseDate(releaseDate)
                .build();
    }
}
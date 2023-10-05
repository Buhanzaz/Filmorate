package ru.yandex.practicum.filmorate.repository.db;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


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
        return addGenre(films);
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
        int filmId = film.getId();
        film.setGenres(getGenres(filmId));
        return getById(filmId);
    }

    @Override
    public void delete(int filmId) {
        /*jdbcTemplate.update("DELETE FROM FILMS WHERE FILM_ID = ?", filmId);*/
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

    private Set<Genre> getGenres(int filmId) {
        Comparator<Genre> compId = Comparator.comparing(Genre::getId);
        Set<Genre> genres = new TreeSet<>(compId);
        String sqlQuery = "SELECT FILM_GENRES.GENRE_ID, GENRES.GENRE FROM FILM_GENRES "
                + "JOIN GENRES ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID "
                + "WHERE FILM_ID = ? ORDER BY GENRE_ID ASC";
        genres.addAll(jdbcTemplate.query(sqlQuery, this::makeGenre, filmId));
        return genres;
    }

    private void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
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
        String sqlQuery = "SELECT * FROM FILMS "
                + "LEFT JOIN likes ON likes.FILM_ID = FILMS.FILM_ID "
                + "JOIN MPA_RATING ON FILMS.MPA_RATING_ID = MPA_RATING.RATING_ID "
                + "GROUP BY FILMS.FILM_ID "
                + "ORDER BY COUNT (likes.FILM_ID) DESC "
                + "LIMIT "
                + count;
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        return addGenre(films);
    }

    private List<Film> addGenre(List<Film> films) {
       /* Map<Integer, Film> filmsTable = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
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
        return films;*/
        List<FilmGenre> filmGenres =
                jdbcTemplate.query(
                        "select FILM_GENRES.FILM_ID, GENRES.GENRE_ID, GENRES.GENRE "
                                + "from FILMS, FILM_GENRES, GENRES "
                                + "where FILMS.FILM_ID = FILM_GENRES.FILM_ID "
                                + "and FILM_GENRES.GENRE_ID = GENRES.GENRE_ID",
                        (rs, rownum) -> new FilmGenre(rs.getInt(1), rs.getInt(2), rs.getString(3)));

        films.forEach(film -> {
            filmGenres.stream()
                    .filter(filmGenre -> film.getId() == filmGenre.filmId)
                    .forEach(filmGenre -> film.addGenre(new Genre(filmGenre.genreId, filmGenre.genreName)));
        });
        return films;
    }

    @AllArgsConstructor
    private static class FilmGenre {
        int filmId;
        int genreId;
        String genreName;
    }

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre");
        return new Genre(genreId, genreName);
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
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .releaseDate(releaseDate)
                .build();
    }
}
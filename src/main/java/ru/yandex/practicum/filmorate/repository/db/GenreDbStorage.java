package ru.yandex.practicum.filmorate.repository.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @Override
    public Genre getGenreById(int genreId) {
        String sglQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sglQuery, genreId);
        if (rowSet.next()) {
            return new Genre(genreId, rowSet.getString("genre"));
        }
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "SELECT * FROM GENRES";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        while (rowSet.next()) {
            genres.add(new Genre(rowSet.getInt("genre_id"), rowSet.getString("genre")));
        }
        return genres;
    }
}
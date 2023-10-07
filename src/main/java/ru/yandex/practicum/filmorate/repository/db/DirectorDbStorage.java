package ru.yandex.practicum.filmorate.repository.db;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.interfaces.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(Integer id) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Director with ID=%d not found", id));
        }
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        int directorId = simpleJdbcInsert.executeAndReturnKey(Map.of("name", director.getName())).intValue();
        director.setId(directorId);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId());

        String sql = "UPDATE DIRECTORS "
                + "SET NAME = ? "
                + "WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirectorById(Integer id) {
        getDirectorById(id);
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");

        return new Director(id, name);
    }
}

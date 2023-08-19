package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Getter
@Repository
public class FilmStorage {
    private final Map<Integer, Film> movies;
    private int id;

    public FilmStorage() {
        this.movies = new HashMap<>();
        this.id = 1;
    }

    public Film add(Film film) {
        film.setId(id);
        movies.put(id, film);
        id++;
        return film;
    }

    public void put(Film film) {
        movies.put(film.getId(), film);
    }

}

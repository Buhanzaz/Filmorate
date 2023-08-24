package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.UuidGenerator;

import java.util.HashMap;
import java.util.Map;

@Getter
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<String, Film> movies;
    private final UuidGenerator uuidGenerator;

    @Autowired
    public InMemoryFilmStorage(UuidGenerator uuidGenerator) {
        this.movies = new HashMap<>();
        this.uuidGenerator = uuidGenerator;
    }

    public Film add(Film film) {
        final String id = uuidGenerator.nextUuid();

        film.setId(id);
        movies.put(id, film);
        return film;
    }

    public void put(Film film) {
        movies.put(film.getId(), film);
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.*;

@Getter
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private final IdGenerator uuidGenerator;

    @Autowired
    public InMemoryFilmStorage(IdGenerator uuidGenerator) {
        this.films = new HashMap<>();
        this.uuidGenerator = uuidGenerator;
    }

    public Film add(Film film) {
        if (getFilms().containsKey(film.getId())) {
            throw new UserException("The user is already in the database");
        }

        final int id = uuidGenerator.nextFilmId();

        film.setId(id);
        films.put(id, film);
        return film;
    }

    public void put(Film film) {
        if (!getFilms().containsKey(film.getId())) {
            throw new UserException("The user isn't already in the database");
        }
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilm(int id) {
        return films.get(id);
    }

    public List<Film> getSortedTopTenFilms() {
        return new ArrayList<>(films.values());
    }
}

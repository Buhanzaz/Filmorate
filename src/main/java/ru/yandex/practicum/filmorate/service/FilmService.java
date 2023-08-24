package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final InMemoryFilmStorage storage;

    public void addFilm(Film film) {
        if (storage.getMovies().containsKey(film.getId())) {
            throw new UserException("The user is already in the database");
        }
        storage.add(film);
    }

    public List<Film> getAllFilm() {
        return new ArrayList<>(storage.getMovies().values());
    }

    public void changeFilm(Film film) {
        if (!storage.getMovies().containsKey(film.getId())) {
            throw new UserException("The user isn't already in the database");
        }
        storage.put(film);
    }
}

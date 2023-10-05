package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage extends LikesStorage {
    Film create(Film film);

    Film update(Film film);

    void delete(int id);

    Collection<Film> getAllFilm();

    Film getById(Integer id);

    void addGenre(int filmId, Set<Genre> genres);
}
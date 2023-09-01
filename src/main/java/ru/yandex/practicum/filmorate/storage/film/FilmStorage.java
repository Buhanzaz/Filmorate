package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Film add(Film film);
    void put(Film film);
    Map<String, Film> getMovies();
}
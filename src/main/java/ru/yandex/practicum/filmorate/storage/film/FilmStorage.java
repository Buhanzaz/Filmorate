package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

@Component
public interface FilmStorage {
    Film add(Film film);

    void put(Film film);

    Map<Integer, Film> getFilms();

    Film getFilm(int id);
}

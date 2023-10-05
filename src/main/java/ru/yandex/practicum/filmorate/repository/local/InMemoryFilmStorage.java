package ru.yandex.practicum.filmorate.repository.local;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.local.UserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;

import java.util.*;

@Getter
@Repository
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private final IdGenerator uuidGenerator;

    @Autowired
    public InMemoryFilmStorage(IdGenerator uuidGenerator) {
        this.films = new HashMap<>();
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public Film create(Film film) {
        if (getFilms().containsKey(film.getId())) {
            throw new UserException("The user is already in the database");
        }

        final int id = uuidGenerator.nextFilmId();

        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!getFilms().containsKey(film.getId())) {
            throw new UserException("The user isn't already in the database");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        films.remove(id);
    }

    @Override
    public Collection<Film> getAllFilm() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
    }

    @Override
    public void addGenre(int filmId, Set<Genre> genres) {
        films.get(filmId).setGenres((SortedSet<Genre>) genres);
    }

    @Override
    public void addLike(int filmId, int userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        films.get(filmId).deleteLike(userId);
    }
}

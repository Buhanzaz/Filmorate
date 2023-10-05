package ru.yandex.practicum.filmorate.repository.local;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.local.UserException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

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
    public String delete(int id) {
        films.remove(id);
        return "";
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
        films.get(filmId).setGenres(genres);
    }

    @Override
    public void addLike(int filmId, int userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        films.get(filmId).deleteLike(userId);
    }

    @Override
    public List<Film> getTopFilm(Integer count) {
        return new ArrayList<>(films.values().stream()
                .sorted((f1, f2) -> {
                    if (f1.getLikes().size() == f2.getLikes().size()) {
                        return 0;
                    }
                    return (f1.getLikes().size() > f2.getLikes().size()) ? -1 : 1;
                })
                .limit(count)
                .collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(Integer directorId) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(int directorId) {
        return new ArrayList<>(films.values().stream()
                .filter(film -> {
                    for (Director filmDirector : film.getDirectors()) {
                        if (filmDirector.getId() == directorId) {
                            return true;
                        }
                    }
                    return false;
                })
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .collect(Collectors.toUnmodifiableList()));
    }
}

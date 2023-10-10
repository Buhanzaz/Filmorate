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

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(Integer directorId) {
        return new ArrayList<>(films.values().stream()
                .filter(film -> {
                    for (Director filmDirector : film.getDirectors()) {
                        if (filmDirector.getId() == directorId) {
                            return true;
                        }
                    }
                    return false;
                })
                .sorted(Comparator.comparing(Film::countLikes))
                .collect(Collectors.toUnmodifiableList()));
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

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        return null;
    }
  
    public List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector) {
        return films.values().stream()
                .filter(film -> {
                    if (searchByTitle && film.getName().contains(query)) {
                        return true;
                    }
                    if (!searchByDirector) {
                        return false;
                    }
                    for (Director director : film.getDirectors()) {
                        if (director.getName().contains(query)) {
                            return true;
                        }
                    }
                    return false;
                }).sorted(Comparator.comparing(Film::countLikes)).collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsByUserId(int userId) {
        return films.values().stream()
                .filter(film -> film.getLikes().contains(userId))
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.SearchBy;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       DirectorStorage directorStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> getAll() {
        log.info("List of all movies: " + filmStorage.getAllFilm().size());
        return filmStorage.getAllFilm();
    }

    public Film create(Film film) {
        Film result = filmStorage.create(film);
        log.info("Movie successfully added: " + film);
        return result;
    }

    public Film update(Film film) {
        Film result = filmStorage.update(film);
        log.info("Movie successfully updated: " + film);
        return result;
    }

    public void delete(int filmId) {
        if (getById(filmId) == null) {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
        log.info("Deleted film with id: {}", filmId);
        filmStorage.delete(filmId);
    }

    public Film getById(Integer id) {
        log.info("Requested user with ID = " + id);
        return filmStorage.getById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        if (film != null) {
            if (userStorage.getById(userId) != null) {
                filmStorage.addLike(filmId, userId);
                log.info("Like successfully added");
            } else {
                throw new NotFoundException("User with ID = " + userId + " not found");
            }
        } else {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        if (film != null) {
            if (userStorage.getById(userId) != null) {
                filmStorage.removeLike(filmId, userId);
                log.info("Like successfully removed");
            } else {
                throw new NotFoundException("User with ID = " + userId + " not found");
            }
        } else {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
    }

    public List<Film> getTopFilm(int volume) {
        log.info("Requested a list of popular movies");
        return getAll().stream().sorted(Comparator.comparingInt(Film::countLikes).reversed())
                .limit(volume).collect(Collectors.toList());
    }

    public List<Film> getDirectorFilm(int directorId, String sortType) {
        log.info("Requested a list of films of director with id {}, sorted by {}", directorId, sortType);
        directorStorage.getDirectorById(directorId);
        switch (sortType) {
            case "year":
                return filmStorage.getDirectorFilmsSortedByYear(directorId);
            case "likes":
                return filmStorage.getDirectorFilmsSortedByLikes(directorId);
            default:
                throw new NotFoundException(String.format("The type of sorting: %s not found", sortType));
        }
    }

    public List<Film> searchFilms(String query, String searchBy) {
        log.info("Requested most popular films, query = {}, searchBy = {}", query, searchBy);

        if (query == null || searchBy == null) {
            return getTopFilm(Integer.MAX_VALUE);
        }
        Set<SearchBy> searchParams = new HashSet<>();
        if (searchBy.contains("title")) {
            searchParams.add(SearchBy.TITLE);
        }
        if (searchBy.contains("director")) {
            searchParams.add(SearchBy.DIRECTOR);
        }
        if (searchParams.isEmpty()) {
            throw new NotFoundException(String.format("Search parameters %s not found", searchBy));
        }
        return filmStorage.searchFilms(query, searchParams);
    }
}
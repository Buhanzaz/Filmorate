package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       GenreService genreService,
                       DirectorStorage directorStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.genreService = genreService;
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

    public List<Film> getTopFilm(int count, Integer genreId, Integer year) {
        log.info("Requested a list of popular movies");
        List<Film> topFilms = new ArrayList<>(getAll());

        if (genreId != null) {
            topFilms = topFilms.stream()
                    .filter(film -> film.getGenres().contains(genreService.getGenreById(genreId)))
                    .collect(Collectors.toList());
        }
        if (year != null) {
            topFilms = topFilms.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
        }

        return topFilms.stream().sorted(Comparator.comparingInt(Film::countLikes).reversed())
                .limit(count).collect(Collectors.toList());
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

    public List<Film> getCommonFilms(int userId, int friendId) {
        userStorage.getById(userId);
        List<Film> userFilms = filmStorage.getFilmsByUserId(userId);

        userStorage.getById(friendId);
        List<Film> friendFilms = filmStorage.getFilmsByUserId(friendId);

        return userFilms.stream()
                .filter(friendFilms::contains)
                .sorted(Comparator.comparing(Film::countLikes).reversed())
                .collect(Collectors.toList());
    }
}
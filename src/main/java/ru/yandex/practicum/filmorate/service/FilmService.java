package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
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
}
package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @PostMapping
    public Film postRequestFilm(@Valid @RequestBody Film film) {
        service.addFilm(film);
        log.debug("The movie is added{}: ", film.getName());
        return film;
    }

    @GetMapping()
    public List<Film> getRequestAllFilm() {
        List<Film> allFilm = service.getAllFilm();
        log.debug("Amount of movies: {}", allFilm.size());
        return allFilm;
    }

    @PutMapping
    public Film putRequestFilm(@Valid @RequestBody Film film) {
        service.changeFilm(film);
        log.debug("The movie is changed{}: ", film.getName());
        return film;
    }
}

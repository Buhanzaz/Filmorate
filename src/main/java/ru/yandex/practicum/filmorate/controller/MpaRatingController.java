package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaRatingController {
    private final RatingMpaService ratingMpaService;

    public MpaRatingController(RatingMpaService ratingMpaService) {
        this.ratingMpaService = ratingMpaService;
    }

    @GetMapping
    public List<RatingMpa> getRatingsMpa() {
        log.info("Request for all MPas");
        return ratingMpaService.getRatingsMpa();
    }

    @GetMapping("/{id}")
    public RatingMpa getRatingMpaById(@PathVariable Integer id) {
        log.info("Request to get an MPA with id {}", id);
        return ratingMpaService.getRatingMpaById(id);
    }
}
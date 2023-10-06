package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable int id) {
        reviewService.removeById(id);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable int id) {
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> findAllOrByFilmId(@RequestParam(required = false) String filmId,
                                          @RequestParam(defaultValue = "10") String count) {
        return reviewService.findAllOrByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addLike(id, userId, 1);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addLike(id, userId, -1);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.removeLike(id, userId);
    }
}

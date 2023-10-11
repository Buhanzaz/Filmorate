package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    private void checkUserOrFilmExists(Integer userId, Integer filmId) {
        log.info("Check if USER with ID = {} exists", userId);
        userStorage.getById(userId);
        log.info("Check if FILM with ID = {} exists", userId);
        filmStorage.getById(filmId);
    }

    public Review add(Review review) {
        checkUserOrFilmExists(review.getUserId(), review.getFilmId());

        log.info("Trying to add REVIEW: {}", review);
        return reviewStorage.add(review);
    }

    public Review findById(Integer id) {
        try {
            log.info("Find REVIEW with ID = {} exists", id);
            return reviewStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("REVIEW with ID = %s does not exists", id));
        }
    }

    public List<Review> findAllOrByFilmId(Integer id, Integer count) {
        if (id == null) {
            log.info("Get REVIEWS for all FILMS");
            return reviewStorage.findAll();
        } else {
            log.info("Get REVIEWS for FILM with ID = {}", id);
            return reviewStorage.findAllByFilmId(id, count);
        }
    }

    public Review update(Review review) {
        log.info("Update REVIEW with ID = {}", review.getReviewId());
        if (reviewStorage.update(review) == 0) {
            throw new NotFoundException(
                    String.format("REVIEW with ID = %s does not exists", review.getReviewId()));
        }
        return findById(review.getReviewId());
    }

    public Review addLike(Integer id, Integer userId, int grade) {
        checkUserAndLikeExists(id, userId);

        log.info("USER with ID = {} rate ({}) REVIEW with ID = {}", userId, grade, id);
        return reviewStorage.addLike(id, userId, grade);
    }

    public Review removeLike(Integer id, Integer userId) {
        log.info("Remove REVIEW with ID = {} from USER with ID = {}", id, userId);
        if (reviewStorage.removeLike(id, userId) == 0) {
            checkUserAndLikeExists(id, userId);
        }
        return findById(id);
    }

    public void removeById(Integer id) {
        log.info("Remove REVIEW with ID = {}", id);
        if (reviewStorage.removeById(id) == 0) {
            throw new NotFoundException(
                    String.format("REVIEW with ID = %s does not exists", id));
        }
    }

    private void checkUserAndLikeExists(Integer reviewId, Integer userId) {
        findById(reviewId);
        userStorage.getById(userId);
    }
}

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

    public List<Review> findAllOrByFilmId(String id, String count) {
        int intCount = Integer.parseInt(count);
        if (id == null) {
            log.info("Get REVIEWS for all FILMS");
            return reviewStorage.findAll();
        } else {
            int intId = Integer.parseInt(id);
            log.info("Get REVIEWS for FILM with ID = {}", intId);
            return reviewStorage.findAllByFilmId(intId, intCount);
        }
    }

    public Review update(Review review) {
        log.info("Check if REVIEW with ID = {} exists", review.getReviewId());
        findById(review.getReviewId());

        log.info("Update REVIEW with ID = {}", review.getReviewId());
        return reviewStorage.update(review);
    }

    public Review addLike(Integer id, Integer userId, int grade) {
        checkUserAndLikeExists(id, userId);

        log.info("USER with ID = {} rate ({}) REVIEW with ID = {}", userId, grade, id);
        return reviewStorage.addLike(id, userId, grade);
    }

    public Review removeLike(Integer id, Integer userId) {
        checkUserAndLikeExists(id, userId);

        log.info("Remove REVIEW with ID = {} from USER with ID = {}", id, userId);
        return reviewStorage.removeLike(id, userId);
    }

    public void removeById(Integer id) {
        log.info("Check if REVIEW with ID = {} exists", id);
        findById(id);

        log.info("Remove REVIEW with ID = {}", id);
        reviewStorage.removeById(id);
    }

    private void checkUserAndLikeExists(Integer reviewId, Integer userId) {
        log.info("Check if REVIEW with ID = {} exists", reviewId);
        findById(reviewId);

        log.info("Check if USER with ID = {} exists", userId);
        userStorage.getById(userId);
    }
}

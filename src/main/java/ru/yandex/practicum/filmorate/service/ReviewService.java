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
        log.info("Проверяем существование пользователя с ID = {}", userId);
        filmStorage.getById(userId);
        log.info("Проверяем существование фильма с ID = {}", userId);
        userStorage.getById(filmId);
    }

    public Review add(Review review) {
        checkUserOrFilmExists(review.getUserId(), review.getFilmId());

        log.info("Пытаемся добавить новый отзыв: {}", review);
        return reviewStorage.add(review);
    }

    public Review findById(Integer id) {
        try {
            log.info("Ищем отзыв с ID = {}", id);
            return reviewStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Отзыв с ID = %s не существует", id));
        }
    }

    public List<Review> findAllOrByFilmId(String id, String count) {
        int intCount = Integer.parseInt(count);
        if (id == null) {
            log.info("Полчаем отзывы по фильмам");
            return reviewStorage.findAll();
        } else {
            int intId = Integer.parseInt(id);
            log.info("Получаем отзывы по фильму с ID = {}", intId);
            return reviewStorage.findAllByFilmId(intId, intCount);
        }
    }

    public Review update(Review review) {
        log.info("Обновляем фильм с ID = {}", review.getReviewId());
        return reviewStorage.update(review);
    }

    public Review addLike(Integer id, Integer userId, int grade) {
        log.info("Пользователь ID = {} отгеагировал ({}) на отзыв ID = {}", userId, grade, id);
        return reviewStorage.addLike(id, userId, grade);
    }

    public Review removeLike(Integer id, Integer userId) {
        log.info("Удаляем отзыв ID = {} от пользователя ID = {}", id, userId);
        return reviewStorage.removeLike(id, userId);
    }

    public void removeById(Integer id) {
        log.info("Удаляем отзыв ID = {}", id);
        reviewStorage.removeById(id);
    }
}

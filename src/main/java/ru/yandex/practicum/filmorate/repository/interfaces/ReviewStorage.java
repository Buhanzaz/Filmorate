package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    void removeById(Integer id);

    List<Review> findAllByFilmId(Integer id, int count);

    Review findById(Integer id);

    Review addLike(Integer id, Integer userId, int grade);

    Review removeLike(Integer id, Integer userId);

    List<Review> findAll();
}

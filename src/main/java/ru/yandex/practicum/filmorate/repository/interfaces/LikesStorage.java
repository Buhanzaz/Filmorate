package ru.yandex.practicum.filmorate.repository.interfaces;

public interface LikesStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    //List<Film> getTopFilm(Integer count);
}

package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;


@Component
public class IdGenerator {
    private int userId = 1;
    private int filmId = 1;

    public int nextUserId() {
        return userId++;
    }

    public int nextFilmId() {
        return filmId++;
    }
}

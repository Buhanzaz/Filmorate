package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerator {
    private int userId = 1;
    private int filmId = 1;
    public String nextUuid() {
        return UUID.randomUUID().toString();
    }

    public int nextUserId() {
        return userId++;
    }

    public int nextFilmId() {
        return filmId++;
    }
}

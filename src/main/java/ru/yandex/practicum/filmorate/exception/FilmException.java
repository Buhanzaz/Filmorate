package ru.yandex.practicum.filmorate.exception;

import java.io.IOException;

public class FilmException extends IOException {
    String error;
    String description;

    public FilmException(String message) {
        super(message);
    }

    public FilmException(String error, String description) {
        this.error = error;
        this.description = description;
    }
}

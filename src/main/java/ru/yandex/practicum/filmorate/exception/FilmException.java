package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class FilmException extends RuntimeException {
    private String error;
    private String description;

    public FilmException(String message) {
        super(message);
    }

    public FilmException(String error, String description) {
        this.error = error;
        this.description = description;
    }
}

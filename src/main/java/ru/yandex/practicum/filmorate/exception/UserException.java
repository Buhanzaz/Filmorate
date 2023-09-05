package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private String error;
    private String description;

    public UserException(String message) {
        super(message);
    }

    public UserException(String error, String description) {
        this.error = error;
        this.description = description;
    }

}

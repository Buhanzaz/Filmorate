package ru.yandex.practicum.filmorate.exception;

public class UserException extends RuntimeException {
    String error;
    String description;

    public UserException(String message) {
        super(message);
    }

    public UserException(String error, String description) {
        this.error = error;
        this.description = description;
    }
}

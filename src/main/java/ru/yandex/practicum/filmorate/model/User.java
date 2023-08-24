package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    //@PositiveOrZero(message = "Id cannot be a negative number")
    private String id;

    @NotBlank
    @NotNull
    @Email
    private final String email;

    @NotBlank
    @NotNull
    private final String login;

    private String name;

    @PastOrPresent
    private final LocalDate birthday;
}

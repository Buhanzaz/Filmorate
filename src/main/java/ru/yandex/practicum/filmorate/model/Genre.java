package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Genre {
    @Positive
    private int id;

    @NotBlank
    private String name;

}

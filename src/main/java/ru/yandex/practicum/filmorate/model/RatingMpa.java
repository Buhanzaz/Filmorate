package ru.yandex.practicum.filmorate.model;



import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class RatingMpa {

    @Positive
    protected int id;

    @NotBlank
    protected String name;
}
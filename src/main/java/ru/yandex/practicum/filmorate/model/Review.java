package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Review {
    private Integer reviewId;
    @NotNull
    private Integer filmId;
    @NotNull
    private Integer userId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    private Integer useful;
}

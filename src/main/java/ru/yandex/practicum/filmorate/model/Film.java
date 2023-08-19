package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.AfterDate;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    @PositiveOrZero(message = "Id cannot be a negative number")
    private int id;

    @NotBlank(message = "Name film is mandatory")
    private final String name;

    @Length(max = 200, message = "The description is too long. the maximum number of characters is 200")
    private final String description;

    @AfterDate(message = "ada")
    private LocalDate releaseDate;

    @NotNull(message = "Duration film is not empty")
    @PositiveOrZero(message = "Duration cannot be a negative number")
    private final int duration;
}

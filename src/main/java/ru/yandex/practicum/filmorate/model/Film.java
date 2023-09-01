package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.AfterDate;
import ru.yandex.practicum.filmorate.exception.FilmException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
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

    @Setter(AccessLevel.NONE)
    private Set<Integer> usersWhoLikeIt = new HashSet<>();

    @Setter(AccessLevel.NONE)
    private int likes = 0;

    @SneakyThrows
    public void addLike(int idUser) {
        try {
                usersWhoLikeIt.add(idUser);
                likes++;
        } catch (Exception e) {
            throw new FilmException("Have you already liked this movie");
        }
    }

    @SneakyThrows
    public void removeLike(int idUser) {
        try {
            if (usersWhoLikeIt.contains(idUser)) {
                usersWhoLikeIt.remove(idUser);
                likes--;
            }
        } catch (Exception e) {
            throw new FilmException("Have you already liked this movie");
        }
    }

}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
public class Film {
    @PositiveOrZero(message = "Id cannot be a negative number")
    private int id;

    @NotBlank(message = "Name film is mandatory")
    private String name;

    @NotBlank
    @Length(max = 200, message = "The description is too long. the maximum number of characters is 200")
    private String description;

    @NotNull
    @AfterDate
    private LocalDate releaseDate;

    @NotNull(message = "Duration film is not empty")
    @PositiveOrZero(message = "Duration cannot be a negative number")
    private int duration;

    private Set<Genre> genres;
    private Set<Director> directors;

    private RatingMpa mpa;

    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(Integer id) {
        likes.add(id);
    }

    public void deleteLike(Integer id) {
        likes.remove(id);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
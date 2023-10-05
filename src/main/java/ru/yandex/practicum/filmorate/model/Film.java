package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.*;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
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
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private SortedSet<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

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

    public int countLikes() {
        return likes.size();
    }
}

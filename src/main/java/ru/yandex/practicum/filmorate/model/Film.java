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
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && duration == film.duration && Objects.equals(name, film.name) && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate) && Objects.equals(genres, film.genres) && Objects.equals(directors, film.directors) && Objects.equals(mpa, film.mpa) && Objects.equals(likes, film.likes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration, genres, directors, mpa, likes);
    }
}
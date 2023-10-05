package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Builder(toBuilder = true)
@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre implements Comparable<Genre> {

    @Positive
    protected int id;

    @NotBlank
    protected String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id && Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public int compareTo(Genre o) {
        return this.id - o.getId();
    }
}

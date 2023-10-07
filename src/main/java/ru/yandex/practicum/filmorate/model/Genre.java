package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class Genre implements Comparable<Genre> {

    @Positive
    protected int id;

    @NotBlank
    protected String name;

    @Override
    public int compareTo(Genre o) {
        return this.id - o.getId();
    }
}

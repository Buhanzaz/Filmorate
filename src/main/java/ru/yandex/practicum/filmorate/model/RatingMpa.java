package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingMpa {

    @Positive
    protected int id;

    @NotBlank
    protected String name;
}
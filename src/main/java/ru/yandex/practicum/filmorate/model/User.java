package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class User {
    @PositiveOrZero(message = "Id cannot be a negative number")
    private int id;

    @NotBlank
    @NotNull
    private String login;

    private String name;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @PastOrPresent
    @NotNull
    private LocalDate birthday;

    @JsonIgnore
    private final Set<Integer> friendIds = new HashSet<>();

    public void addFriend(Integer id) {
        friendIds.add(id);
    }

    public void deleteFriend(Integer id) {
        friendIds.remove(id);
    }
}
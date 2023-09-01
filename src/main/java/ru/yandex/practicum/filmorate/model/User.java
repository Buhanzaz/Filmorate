package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @PositiveOrZero(message = "Id cannot be a negative number")
    private int id;

    @NotBlank
    @NotNull
    private final String login;

    private String name;

    @NotBlank
    @NotNull
    @Email
    private final String email;

    @PastOrPresent
    private final LocalDate birthday;

    @Setter(AccessLevel.NONE)
    private Set<Integer> idFriends = new HashSet<>();

    public void addFriend(int idUser) {
        idFriends.add(idUser);
    }

    public void removeFriend(int idUser) {
        idFriends.remove(idUser);
    }
}

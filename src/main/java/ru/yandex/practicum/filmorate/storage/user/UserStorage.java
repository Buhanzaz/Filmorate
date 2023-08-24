package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User add(User user);
    void put(User user);
    Map<String, Film> getUsers();
}

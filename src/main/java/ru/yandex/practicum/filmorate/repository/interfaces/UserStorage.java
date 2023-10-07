package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends FriendsStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    void delete(int id);

    User getById(Integer id);
}
package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage extends FriendsStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    void delete(Integer id);

    User getById(Integer id);

    List<Event> getLogEvents(Integer userId);
}
package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Component
public interface UserStorage {
    User add(User user);

    void put(User user);

    Map<Integer, User> getUsers();

    User getUser(int id);

    void setUserNameByLogin(User user, String text);
}

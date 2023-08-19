package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Getter
@Repository
public class UserStorage {
    protected final Map<Integer, User> users;
    private int id;

    public UserStorage() {
        this.users = new HashMap<>();
        this.id = 1;
    }

    public User add(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        return user;
    }

    public void put(User user) {
        users.put(user.getId(), user);
    }
}

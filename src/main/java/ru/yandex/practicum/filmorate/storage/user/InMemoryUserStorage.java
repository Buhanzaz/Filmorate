package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Repository
public class InMemoryUserStorage implements UserStorage {
    protected final Map<Integer, User> users;
    private final IdGenerator uuidGenerator;

    @Autowired
    public InMemoryUserStorage(IdGenerator uuidGenerator) {
        this.users = new HashMap<>();
        this.uuidGenerator = uuidGenerator;
    }

    public User add(User user) {
        if (getUsers().containsKey(user.getId())) {
            throw new UserException("The user is already in the database");
        }

        final int id = uuidGenerator.nextUserId();

        user.setId(id);
        users.put(id, user);
        return user;
    }

    public void put(User user) {
        if (!getUsers().containsKey(user.getId())) {
            throw new UserException("The user isn't already in the database");
        }

        users.put(user.getId(), user);
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    public void setUserNameByLogin(User user, String text) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("The user is {}: {}", text, user.getName());
    }
}

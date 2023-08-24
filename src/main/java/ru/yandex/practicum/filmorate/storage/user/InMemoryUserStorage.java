package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UuidGenerator;

import java.util.HashMap;
import java.util.Map;

@Getter
@Repository
public class InMemoryUserStorage implements UserStorage {
    protected final Map<String, User> users;
    private final UuidGenerator uuidGenerator;

    @Autowired
    public InMemoryUserStorage(UuidGenerator uuidGenerator) {
        this.users = new HashMap<>();
        this.uuidGenerator = uuidGenerator;
    }

    public User add(User user) {
        final String id = uuidGenerator.nextUuid();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public void put(User user) {
        users.put(user.getId(), user);
    }
}

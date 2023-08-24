package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage storage;

    public void addUser(User user) {
        if (storage.getUsers().containsKey(user.getId())) {
            throw new UserException("The user is already in the database");
        }
        storage.add(user);
    }

    public List<User> getAllUser() {
        return new ArrayList<>(storage.getUsers().values());
    }

    public void changeUser(User user) {
        if (!storage.getUsers().containsKey(user.getId())) {
            throw new UserException("The user isn't already in the database");
        }
        storage.put(user);
    }
}

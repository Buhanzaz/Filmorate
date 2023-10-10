package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;

@Component
@Service
@Slf4j
public class UserService {
    private final UserStorage storage;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage,
                       @Qualifier("FilmDbStorage") FilmDbStorage filmDbStorage) {
        this.storage = storage;
        this.filmDbStorage = filmDbStorage;
    }

    public Collection<User> getAll() {
        log.info("List of all users: " + storage.getAll().size());
        return storage.getAll();
    }

    public User create(User user) {
        changeName(user);
        User result = storage.create(user);
        log.info("User successfully added: " + user);
        return result;
    }

    public User update(User user) {
        changeName(user);
        User result = storage.update(user);
        log.info("User successfully updated: " + user);
        return result;
    }

    public void delete(int userId) {
        if (getById(userId) == null) {
            throw new NotFoundException("User with ID = " + userId + " not found");
        }
        log.info("Deleted film with id: {}", userId);
        storage.delete(userId);
    }

    public User getById(Integer id) {
        log.info("Requested user with ID = " + id);
        return storage.getById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        checkUser(userId, friendId);
        storage.addFriend(userId, friendId);

        log.info("Friend successfully added");
    }

    public void removeFriend(Integer userId, Integer friendId) {
        checkUser(userId, friendId);
        storage.removeFriend(userId, friendId);
        log.info("Friend successfully removed");
    }

    public List<User> getAllFriends(Integer userId) {
        checkUser(userId, userId);
        List<User> result = storage.getFriendsById(userId);
        log.info("Friends of user with ID = " + userId + result);
        return result;
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        checkUser(userId, friendId);
        List<User> result = storage.getCommonFriends(userId, friendId);
        log.info("Common friends of users with ID " + " {} and {} {} ", userId, friendId, result);
        return result;
    }

    public List<Film> getRecommendedFilmsForUser(int userId) {
        getById(userId);
        return filmDbStorage.getRecommendedFilms(userId);
    }

    private void changeName(User user) {
        if (user.getName() == null | user.getName().isEmpty() | user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkUser(Integer userId, Integer friendId) {
        storage.getById(userId);
        storage.getById(friendId);
    }
}
package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int id, int friendId) {
        checkingTheExistenceOfUsers(id, friendId);

        userStorage.getUsers().get(id).addFriend(friendId);
        userStorage.getUsers().get(friendId).addFriend(id);
    }

    public void removeFriend(int id, int friendId) {
        checkingTheExistenceOfUsers(id, friendId);

        userStorage.getUsers().get(id).removeFriend(friendId);
        userStorage.getUsers().get(friendId).removeFriend(id);
    }

    public List<User> listAllFriends(int id) {
        return userStorage.getUser(id).getIdFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> listOfMutualFriends(int id, int friendId) {
        checkingTheExistenceOfUsers(id, friendId);

        List<Integer> userIdFriendsList = new ArrayList<>(userStorage.getUsers().get(id).getIdFriends());
        List<Integer> friendIdFriendsList = new ArrayList<>(userStorage.getUsers().get(friendId).getIdFriends());
        List<User> mutualFriends = new ArrayList<>();

        for (Integer idFriend : userIdFriendsList) {
            if (friendIdFriendsList.contains(idFriend)) {
                mutualFriends.add(userStorage.getUsers().get(idFriend));
            }
        }
        return mutualFriends;
    }

    private void checkingTheExistenceOfUsers(int id, int friendId) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new UserException("The user with this id was not found.");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new UserException("You are trying to add a non-existent user, check the correctness of the id.");
        }
    }
}

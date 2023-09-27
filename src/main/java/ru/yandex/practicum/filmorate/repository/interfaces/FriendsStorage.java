package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(int userID, int friendId);

    void removeFriend(int userID, int friendId);

    List<User> getFriendsById(int userId);

    List<User> getCommonFriends(int friend1, int friend2);

    boolean isFriend(int userId, int friendId);
}
package ru.yandex.practicum.filmorate.repository.local;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.local.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.*;

@Slf4j
@Getter
@Repository
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    protected final Map<Integer, User> users;
    private final IdGenerator uuidGenerator;

    @Autowired
    public InMemoryUserStorage(IdGenerator uuidGenerator) {
        this.users = new HashMap<>();
        this.uuidGenerator = uuidGenerator;
    }


    @Override
    public void addFriend(int userID, int friendId) {
        users.get(userID).addFriend(friendId);
    }

    @Override
    public void removeFriend(int userID, int friendId) {
        users.get(userID).deleteFriend(friendId);
    }

    @Override
    public List<User> getFriendsById(int userId) {
        List<User> userList = new ArrayList<>();
        for (Integer friendId : users.get(userId).getFriendIds()) {
            userList.add(users.get(friendId));
        }
        return userList;
    }

    @Override
    public List<User> getCommonFriends(int friend1, int friend2) {
        List<User> friendsList = new ArrayList<>();

        for (Integer friendId : users.get(friend1).getFriendIds()) {
            for (Integer friendId2 : users.get(friend1).getFriendIds()) {
                if (friendId.equals(friendId2)) {
                    friendsList.add(users.get(friend1));
                }
            }
        }
        return friendsList;
    }

    @Override
    public boolean isFriend(int userId, int friendId) {
        boolean user = users.get(userId).getFriendIds().contains(friendId);
        boolean friend = users.get(friendId).getFriendIds().contains(userId);

        return friend & user;
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (getUsers().containsKey(user.getId())) {
            throw new UserException("The user is already in the database");
        }

        final int id = uuidGenerator.nextUserId();

        user.setId(id);
        users.put(id, user);
        if (user.getName() == null) {
            setUserNameByLogin(user, user.getLogin());
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!getUsers().containsKey(user.getId())) {
            throw new UserException("The user isn't already in the database");
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public String delete(int id) {
        users.remove(id);
        return "";
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    public void setUserNameByLogin(User user, String text) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("The user is {}: {}", text, user.getName());
    }
}

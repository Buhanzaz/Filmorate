package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.enums.EventType;
import ru.yandex.practicum.filmorate.repository.enums.Operation;
import ru.yandex.practicum.filmorate.repository.interfaces.UserStorage;

import java.util.*;

@Repository
@Qualifier("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LogEventDbStorage logEventDbStorage;

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT * FROM USERS";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        List<User> users = new ArrayList<>();
        while (rowSet.next()) {
            users.add(userMap(rowSet));
        }
        return users;
    }

    @Override
    public User create(User user) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("login", "name", "email", "birthday")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of(
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "birthday", java.sql.Date.valueOf(user.getBirthday())))
                .getKeys();
        if (keys != null) {
            user.setId((Integer) keys.get("user_id"));
        }
        return user;
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        String sqlQuery = "UPDATE USERS "
                + "SET NAME = ?, "
                + "LOGIN = ?, "
                + "EMAIL = ?, "
                + "BIRTHDAY = ? "
                + "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(),
                user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void delete(Integer userId) {
        jdbcTemplate.update("DELETE FROM USERS WHERE USER_ID = ?", userId);
    }

    @Override
    public User getById(Integer userId) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (rowSet.next()) {
            return userMap(rowSet);
        } else {
            throw new NotFoundException("User with ID=" + userId + " not found!");
        }
    }

    @Override
    public List<Event> getLogEvents(Integer userId) {
        String sqlQuery = "SELECT * FROM LOG_EVENT WHERE USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        List<Event> result = new ArrayList<>();

        while (rowSet.next()) {
            result.add(logMap(rowSet));
        }
        return result;
    }


    public void addFriend(int userId, int friendId) {
        String sqlQuery = "MERGE into FRIENDS KEY(USER_ID, FRIEND_ID) VALUES(?, ?, true)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        logEventDbStorage.logging(userId, EventType.FRIEND, Operation.ADD, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        logEventDbStorage.logging(userId, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    public List<User> getFriendsById(int userId) {
        List<User> friends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM USERS "
                + "WHERE USERS.USER_ID IN (SELECT FRIEND_ID FROM FRIENDS "
                + "WHERE USER_ID = ?)";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        while (rowSet.next()) {
            friends.add(UserDbStorage.userMap(rowSet));
        }
        return friends;
    }

    public List<User> getCommonFriends(int friend1, int friend2) {
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM USERS "
                + "WHERE USERS.USER_ID IN (SELECT FRIEND_ID FROM FRIENDS "
                + "WHERE USER_ID IN (?, ?) "
                + "AND FRIENDS.FRIEND_ID NOT IN (?, ?))";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, friend1, friend2, friend1, friend2);
        while (rowSet.next()) {
            commonFriends.add(UserDbStorage.userMap(rowSet));
        }
        return commonFriends;
    }

    public boolean isFriend(int userId, int friendId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE "
                + "USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return rowSet.next();
    }

    private static Event logMap(SqlRowSet rowSet) {
        return Event.builder()
                .timestamp(rowSet.getLong("timestamp"))
                .userId(rowSet.getInt("user_id"))
                .eventType(EventType.valueOf(rowSet.getString("event_type")))
                .operation(Operation.valueOf(rowSet.getString("operation")))
                .eventId(rowSet.getInt("event_id"))
                .entityId(rowSet.getInt("entity_id"))
                .build();
    }

    private static User userMap(SqlRowSet rowSet) {
        return User.builder()
                .id(rowSet.getInt("user_id"))
                .login(rowSet.getString("login"))
                .name(rowSet.getString("name"))
                .email(rowSet.getString("email"))
                .birthday(Objects.requireNonNull(rowSet.getTimestamp("birthday"))
                        .toLocalDateTime().toLocalDate())
                .build();
    }
}
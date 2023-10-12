package ru.yandex.practicum.filmorate.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.repository.enums.EventType;
import ru.yandex.practicum.filmorate.repository.enums.Operation;

@Component
@RequiredArgsConstructor
public class LogEventDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void logging(Integer userId, EventType eventType, Operation operation, Integer entityId) {
        String sqlQuery = "INSERT INTO LOG_EVENT(USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (?, ?, ?, ? )";

        jdbcTemplate.update(sqlQuery, userId, eventType.getEventType(), operation.getOperation(), entityId);
    }
}

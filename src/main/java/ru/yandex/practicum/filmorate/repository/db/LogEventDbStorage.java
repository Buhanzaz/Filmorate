package ru.yandex.practicum.filmorate.repository.db;

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
        String sqlQuery = "INSERT INTO LOG_EVENT(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES ( ?, ?, ?, ?, ? )";

        jdbcTemplate.update(sqlQuery, System.currentTimeMillis(),
                userId, eventType.getEventType(), operation.getOperation(), entityId);
    }
}

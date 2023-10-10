package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.repository.enums.EventType;
import ru.yandex.practicum.filmorate.repository.enums.Operation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
public class Event {
    @NotNull
    private Long timestamp;

    @NotNull
    @Positive
    private Integer userId;

    @NotNull
    private EventType eventType;

    @NotNull
    private Operation operation;

    @NotNull
    @Positive
    private Integer eventId;

    @NotNull
    @Positive
    private Integer entityId;
}

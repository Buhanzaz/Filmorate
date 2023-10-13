package ru.yandex.practicum.filmorate.repository.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Operation {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private final String operation;
}

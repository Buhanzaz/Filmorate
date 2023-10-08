package ru.yandex.practicum.filmorate.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SearchBy {
    TITLE("F.NAME"),
    DIRECTOR("D.NAME");

    public final String fieldName;
}

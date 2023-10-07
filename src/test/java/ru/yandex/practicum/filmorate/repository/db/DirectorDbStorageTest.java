package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorDbStorageTest {
    private final DirectorDbStorage directorStorage;
    Director director1;
    Director director2;

    Director director3;


    @BeforeEach
    void beforeEach() {
        director1 = Director.builder().name("Director1").build();
        director2 = Director.builder().name("Director2").build();
        director3 = Director.builder().name("Director3").build();

        directorStorage.createDirector(director1);
        directorStorage.createDirector(director2);
    }

    @Test
    void shouldReturnAllDirectors() {
        List<Director> directorsFromDb = directorStorage.getAllDirectors();
        assertAll("Проверка полученного списка всех режиссеров",
                () -> assertTrue(directorsFromDb.size() == 2, "Размер полученного" +
                        "списка режиссеров не равен 2"),
                () -> assertEquals("Director1", directorsFromDb.get(0).getName(),
                        "Неверное имя режиссера"),
                () -> assertEquals("Director2", directorsFromDb.get(1).getName(),
                        "Неверное имя режиссера"));
    }

    @Test
    void shouldReturnCorrectDirectorById() {
        Director director = directorStorage.getDirectorById(1);
        Director expectedDirector = new Director(1, "Director1");
        assertEquals(expectedDirector, director, "Полученный режиссер не совпадает с ожидемым");
    }

    @Test
    void shouldThrowExceptionWhenGetDirectorWithNonexistentId() {
        Exception e = assertThrows(NotFoundException.class, () -> {
            directorStorage.getDirectorById(100);
        });
        String expectedMessage = "Director with ID=100 not found";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldCreateNewDirector() {
        Integer addedDirectorId = directorStorage.createDirector(director3).getId();
        Director addedDirectorFromDb = directorStorage.getDirectorById(addedDirectorId);
        assertEquals(director3, addedDirectorFromDb, "Режиссеры не совпадают");
    }

    @Test
    void shouldUpdateDirector() {
        director1.setName("Updated director1");
        directorStorage.updateDirector(director1);
        Director updatedDirectorFromDb = directorStorage.getDirectorById(1);
        assertEquals(director1, updatedDirectorFromDb, "Обновленный директор не совпадает с ожидаемым");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDirectorWithNonExistentId() {
        director1.setId(100);
        Exception e = assertThrows(NotFoundException.class, () -> {
            directorStorage.updateDirector(director1);
        });
        String expectedMessage = "Director with ID=100 not found";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldDeleteDirectorById() {
        directorStorage.deleteDirectorById(1);

        Exception e = assertThrows(NotFoundException.class, () -> {
            directorStorage.getDirectorById(1);
        });
        String expectedMessage = "Director with ID=1 not found";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldThrowExceptionWhenDeleteDirectorWithNonexistentId() {
        Exception e = assertThrows(NotFoundException.class, () -> {
            directorStorage.deleteDirectorById(100);
        });
        String expectedMessage = "Director with ID=100 not found";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.interfaces.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        log.info("Requested list of all directors");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        log.info("Requested director with id {}", id);
        return directorStorage.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        Director result = directorStorage.createDirector(director);
        log.info("The new director was created {}", result);
        return result;
    }

    public Director updateDirector(Director director) {
        Director result = directorStorage.updateDirector(director);
        log.info("Director was updated: {}", result);
        return result;
    }

    public void deleteDirectorById(Integer id) {
        directorStorage.deleteDirectorById(id);
        log.info("Director with id {} was deleted", id);
    }
}

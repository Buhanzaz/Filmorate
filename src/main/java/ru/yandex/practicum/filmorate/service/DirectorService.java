package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.interfaces.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public void deleteDirectorById(Integer id) {
        directorStorage.deleteDirectorById(id);
    }
}

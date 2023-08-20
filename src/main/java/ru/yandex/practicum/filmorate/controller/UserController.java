package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User postRequestUser(@Valid @RequestBody User user) {
        service.addUser(user);
        setUserNameByLogin(user, "added");
        return user;
    }

    @GetMapping()
    public List<User> getRequestAllUser() {
        List<User> allUsers = service.getAllUser();
        log.debug("Amount of movies: {}", allUsers.size());
        return allUsers;
    }

    @PutMapping
    public User putRequestUser(@Valid @RequestBody User user) {
        service.changeUser(user);
        setUserNameByLogin(user, "changed");
        return user;
    }

    private void setUserNameByLogin(User user, String text) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("The user is {}: {}", text, user.getName());
    }
}

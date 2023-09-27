package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUserWithBadEmail_shouldShowErrorMessage() {
        User user = new User(1, "test", "test", "none", LocalDate.now().minusYears(30));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void createUserWithEmptyLogin_shouldShowErrorMessage() {
        User user = new User(1, "test@yandex.ru", " ", "test", LocalDate.now().minusYears(30));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void createFutureBirthUser_shouldShowErrorMessage() {
        User user = new User(1, "test@yandex.ru", "test", "test", LocalDate.now().plusYears(30));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void updateUserToEmptyEmail_shouldShowErrorMessage() {
        User user = new User(1, "test@yandex.ru", "test", "test", LocalDate.now().minusYears(30));
        restTemplate.postForEntity("/users", user, User.class);

        User user2 = new User(1, null, "test", "test", LocalDate.now().minusYears(30));
        HttpEntity<User> entity = new HttpEntity<>(user2);
        ResponseEntity<User> response2 = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }

    @Test
    void updateUserToEmptyLogin_shouldShowErrorMessage() {
        User user = new User(1, "test@yandex.ru", "test", "test", LocalDate.now().minusYears(30));
        restTemplate.postForEntity("/users", user, User.class);
        User user2 = new User(1, "test@yandex.ru", " ", "test", LocalDate.now().minusYears(30));
        HttpEntity<User> entity = new HttpEntity<>(user2);
        ResponseEntity<User> response2 = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }

    @Test
    void updateFutureBirthUser_shouldShowErrorMessage() {
        User usr = new User(1, "test@yandex.ru", "test", "test", LocalDate.now().minusYears(30));
        restTemplate.postForEntity("/users", usr, User.class);
        User user = new User(1, "test@yandex.ru", "test", "test", LocalDate.now().plusYears(30));
        HttpEntity<User> entity = new HttpEntity<>(user);
        ResponseEntity<User> response2 = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }
}
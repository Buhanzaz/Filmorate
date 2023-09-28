package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.db.RatingMpaDbStorage;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmsControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    RatingMpaDbStorage dbStorage;

    @Test
    void createFilmWithEmptyName_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name(null)
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(-180)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void createFilmWithTooLongDescription_shouldShowErrorMessage() {
        String description = "tratata".repeat(200);
        Film film = Film.builder()
                .name("Avatar")
                .description(description)
                .releaseDate(LocalDate.now().minusYears(13))
                .duration(280)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void createFilmWithMinusDuration_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(20))
                .duration(-180)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
    }

    @Test
    void updateFilmWithEmptyName_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        restTemplate.postForEntity("/films", film, Film.class);
        Film film2 = Film.builder()
                .name(null)
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film2);
        ResponseEntity<Film> response2 = restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());

        System.out.println(response2.getBody());
        System.out.println("hello");
    }

    @Test
    void updateFilmWithTooLongDescription_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        restTemplate.postForLocation("/films", film);
        String description = "tratata".repeat(200);
        Film film2 = Film.builder()
                .name("Avatar")
                .description(description)
                .releaseDate(LocalDate.now().minusYears(13))
                .duration(180)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film2);
        ResponseEntity<Film> response2 = restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }

    @Test
    void updateFilmWithMinusDuration_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        restTemplate.postForEntity("/films", film, Film.class);
        Film film2 = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(-180)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film2);
        ResponseEntity<Film> response2 = restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }
}
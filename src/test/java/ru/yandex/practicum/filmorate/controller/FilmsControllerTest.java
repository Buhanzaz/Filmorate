package ru.yandex.practicum.filmorate.controller;


import javax.servlet.ServletException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static String filmNotFail;
    private static String filmFailDescription;
    private static String filmFailDate;
    private static String filmFailDuration;
    private static String filmFailName;
    private static String filmUpdateNotFail;
    private static String filmUpdateUnknown;

    @BeforeAll
    static void beforeAll() {
        filmNotFail = "{\"id\":1,\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";
        filmFailName = "{\"name\":\"\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200}";
        filmFailDescription = "{\"name\":\"Filmname\",\"description\":\"Пятеро друзей(комик-группа «Шарло»)," +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги," +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия»,стал кандидатом Коломбани.\"" +
                ",\"releaseDate\":\"1900-03-25\",\"duration\":200}";
        filmFailDate = "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"1890-03-25\",\"duration\":200}";
        filmFailDuration = "{\"name\":\"Name\",\"description\":\"Descrition\",\"releaseDate\":\"1980-03-25\",\"duration\":-200}";
        filmUpdateNotFail = "{\"id\":1,\"name\":\"Test\",\"description\":\"Test\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";
        filmUpdateUnknown = "{\"id\":9999,\"name\":\"FilmUpdated\",\"releaseDate\":\"1989-04-17\",\"description\":\"Newfilmupdatedecription\",\"duration\":190}";
    }

    @Test
    public void addAndUpdateAndGetAllFilmTest_isNotFail_statusIs200() throws Exception {
        mockMvc.perform(post("/films")
                        .content(filmNotFail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(put("/films")
                        .content(filmUpdateNotFail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(filmUpdateNotFail, result.getResponse().getContentAsString().replaceAll("\\[", "").replaceAll("]", ""));
    }

    @Test
    public void addFilmTest_failName_statusIs4XX() throws Exception {
        mockMvc.perform(post("/films")
                        .content(filmFailName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void addFilmTest_failDescription_statusIs4XX() throws Exception {
        mockMvc.perform(post("/films")
                        .content(filmFailDescription)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void addFilmTest_failDate_statusIs4XX() throws Exception {
        mockMvc.perform(post("/films")
                        .content(filmFailDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void addFilmTest_failDuration_statusIs4XX() throws Exception {
        mockMvc.perform(post("/films")
                        .content(filmFailDuration)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void putFilmTest_filmUpdateUnknown_statusIs500() {
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(put("/films")
                        .content(filmUpdateUnknown)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()));
        assertEquals("Request processing failed; nested exception is" +
                " ru.yandex.practicum.filmorate.exception.UserException: The user isn't already in the database",
                exception.getMessage());
    }
}
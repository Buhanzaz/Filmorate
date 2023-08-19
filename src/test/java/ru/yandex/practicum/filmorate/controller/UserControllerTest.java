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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static String userNotFail;
    private static String userFailEmail;
    private static String userFailDateBirthday;
    private static String userFailName;
    private static String userUpdateNotFail;
    private static String userUpdateUnknown;

    @BeforeAll
    public static void beforeAll() {
        userNotFail = "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}";
        userFailName = "{\"login\":\"doloreullamco\",\"email\":\"yandex@mail.ru\",\"birthday\":\"2446-08-20\"}";
        userFailEmail = "{\"login\":\"doloreullamco\",\"name\":\"\",\"email\":\"mail.ru\",\"birthday\":\"1980-08-20\"}";
        userFailDateBirthday = "{\"login\":\"dolore\",\"name\":\"\",\"email\":\"test@mail.ru\",\"birthday\":\"2446-08-20\"}";
        userUpdateNotFail = "{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"estadipisicing\",\"birthday\":\"1976-09-20\"}";
        userUpdateUnknown = "{\"login\":\"doloreUpdate\",\"name\":\"estadipisicing\",\"id\":9999,\"email\":\"mail@yandex.ru\",\"birthday\":\"1976-09-20\"}";
    }

    @Test
    public void addAndUpdateAndGetAllUserTest_isNotFail_statusIs200() throws Exception {
        mockMvc.perform(post("/users")
                        .content(userNotFail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(put("/users")
                        .content(userUpdateNotFail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(userUpdateNotFail, result.getResponse().getContentAsString().replaceAll("\\[", "").replaceAll("]", ""));
    }

    @Test
    public void addUserTest_failName_statusIs4XX() throws Exception {
        mockMvc.perform(post("/users")
                        .content(userFailName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void addUserTest_failEmail_statusIs4XX() throws Exception {
        mockMvc.perform(post("/users")
                        .content(userFailEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void addUserTest_failDateBirthday_statusIs4XX() throws Exception {
        mockMvc.perform(post("/users")
                        .content(userFailDateBirthday)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }


    @Test
    public void putUserTest_UserUpdateUnknown_statusIs500() {
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(put("/users")
                        .content(userUpdateUnknown)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()));
        assertEquals("Request processing failed; nested exception is" +
                " ru.yandex.practicum.filmorate.exception.UserException: The user isn't already in the database",
                exception.getMessage());
    }
}

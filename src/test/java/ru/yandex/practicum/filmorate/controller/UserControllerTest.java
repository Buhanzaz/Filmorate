package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndReturnUsers() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1946-08-20\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nick Name"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"));

        MvcResult result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals("[{\"id\":1,\"login\":\"dolore\",\"name\":\"Nick Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\",\"idFriends\":[]}]", result.getResponse().getContentAsString());
    }

    @Test
    void createUserWithInvalidLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"dolore ullamco\",\n" +
                                "  \"email\": \"yandex@mail.ru\",\n" +
                                "  \"birthday\": \"2446-08-20\"\n" +
                                "}"))
                .andExpect(status().is(400));
    }


    @Test
    void createUserWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"dolore ullamco\",\n" +
                                "  \"name\": \"\",\n" +
                                "  \"email\": \"mail.ru\",\n" +
                                "  \"birthday\": \"1980-08-20\"\n" +
                                "}"))
                .andExpect(status().is(400));
    }


    @Test
    void createUserWithInvalidBirthday() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"dolore ullamco\",\n" +
                                "  \"name\": \"\",\n" +
                                "  \"email\": \"mail.ru\",\n" +
                                "  \"birthday\": \"1980-08-20\"\n" +
                                "}"))
                .andExpect(status().is(400));
    }

    @Test
    void createUserWithEmptyName() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"common\",\n" +
                                "  \"email\": \"friend@common.ru\",\n" +
                                "  \"birthday\": \"2000-08-20\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("common"));
    }

    @Test
    void updateUser() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1946-08-20\"\n" +
                        "}"));

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"doloreUpdate\",\n" +
                                "  \"name\": \"est adipisicing\",\n" +
                                "  \"id\": 1,\n" +
                                "  \"email\": \"mail@yandex.ru\",\n" +
                                "  \"birthday\": \"1976-09-20\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("est adipisicing"))
                .andExpect(jsonPath("$.login").value("doloreUpdate"));
    }
}

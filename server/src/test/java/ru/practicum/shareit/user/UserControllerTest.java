package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private User user1;

    @BeforeEach
    public void beforeEach() {
        user1 = new User();
        user1.setName("Name1");
        user1.setEmail("Name1@mail.ru");

        User user2 = new User();
        user2.setName("Name2");
        user2.setEmail("Name2@mail.ru");
    }

    @Test
    @SneakyThrows
    public void createUserTest() {
        long userId = 1;
        user1.setId(userId);

        when(userService.createUser(any(UserDto.class)))
                .thenReturn(UserMapper.userToDto(user1));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    @SneakyThrows
    public void updateUserTest() throws Exception {
        long userId = 1;
        user1.setId(userId);
        user1.setName("Name3");

        when(userService.updateUser(any(User.class), any(UserDto.class)))
                .thenReturn(user1);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));

        verify(userService, times(1)).updateUser(any(User.class), any(UserDto.class));
    }

    @Test
    @SneakyThrows
    public void getUserTest() {
        long userId = 1;
        user1.setId(userId);

        when(userService.getUser(userId))
                .thenReturn(user1);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    @SneakyThrows
    public void findAllUsersTest() {
        when(userService.findAllUsers())
                .thenReturn(Collections.emptyList());
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        when(userService.findAllUsers())
                .thenReturn(List.of(user1));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1))));

        verify(userService, times(2)).findAllUsers();
    }

    @Test
    @SneakyThrows
    public void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(any(Long.class));
    }
}

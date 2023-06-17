package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserMapperTest {
    private User user;

    @BeforeEach
    private void beforeEach() {
        user = new User(1L, "Name1", "Name1@mail.ru");
    }

    @Test
    public void toUserDtoTest() {
        UserDto dto = UserMapper.userToDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void toUserModelTest() {
        UserDto dto = new UserDto(1L, "Name2", "Name2@mail.ru");
        User newUser = UserMapper.dtoToUser(dto);

        assertEquals(dto.getId(), newUser.getId());
        assertEquals(dto.getName(), newUser.getName());
        assertEquals(dto.getEmail(), newUser.getEmail());
    }

    @Test
    public void toUserDtoListTest() {
        List<User> users = List.of(user);
        List<UserDto> userDtoList = UserMapper.userToDtoList(users);

        assertFalse(userDtoList.isEmpty());
        assertEquals(users.get(0).getId(), userDtoList.get(0).getId());
        assertEquals(users.get(0).getName(), userDtoList.get(0).getName());
        assertEquals(users.get(0).getEmail(), userDtoList.get(0).getEmail());
    }
}

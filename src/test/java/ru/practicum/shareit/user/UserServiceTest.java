package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    @Autowired
    private final UserService userService;

    private final User user = new User(null, "user", "user@yandex.ru");

    @Test
    void create() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }


    @Test
    void patch() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        userDto.setName("newName");
        userDto.setEmail("new@email.ru");
        UserDto updateUserDto = userService.patch(user.getId(), userDto);

        assertEquals(updateUserDto.getName(), userDto.getName());
        assertEquals(updateUserDto.getEmail(), userDto.getEmail());
    }

    @Test
    void patchNameUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        userDto.setName("newName");
        userDto.setEmail(null);
        UserDto updateUserDto = userService.patch(user.getId(), userDto);

        assertEquals(updateUserDto.getName(), userDto.getName());
        assertEquals(userDto.getEmail(), userDto.getEmail());
    }

    @Test
    void patchMailUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        userDto.setEmail("new@email.ru");
        userDto.setName(null);
        UserDto updateUserDto = userService.patch(user.getId(), userDto);

        assertEquals(userDto.getName(), userDto.getName());
        assertEquals(updateUserDto.getEmail(), userDto.getEmail());
    }

    @Test
    void deleteUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        List<UserDto> usersList = userService.findAll();
        userService.deleteUserById(userDto.getId());
        List<UserDto> usersListAfterDelete = userService.findAll();
        assertEquals(usersList.size(), usersListAfterDelete.size() + 1);
    }

    @Test
    void getUserById() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDtoById = userService.getUserById(userDto.getId());
        assertEquals(user.getId(), userDtoById.getId());
    }

    @Test
    void deleteWrongUserTest() {
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(1L));
    }

    @Test
    void getWrongUserTest() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }


}

package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface UserDao {
    Map<Long, User> getUsers();

    List<UserDto> findAll();

    UserDto create(@Valid User user);

    UserDto getUserbyId(Long id);

    UserDto patch(Long id, UserDto userDto);

    void deleteUserById(Long userId);
}

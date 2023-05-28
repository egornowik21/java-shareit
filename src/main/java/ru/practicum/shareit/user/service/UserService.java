package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getUserById(Long userId);

    UserDto create(@Valid UserDto userDto);

    UserDto patch(Long id, UserDto userDto);

    void deleteUserById(Long userId);
}

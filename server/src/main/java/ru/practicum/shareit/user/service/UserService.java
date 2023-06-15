package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getUserById(Long userId);

    UserDto create(UserDto userDto);

    UserDto patch(Long id, UserDto userDto);

    void deleteUserById(Long userId);
}

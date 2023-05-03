package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto getUserById(Long userId);

    UserDto create(@Valid User user);
    UserDto patch(Long id,UserDto userDto);
    void deleteUserById(Long userId);
}

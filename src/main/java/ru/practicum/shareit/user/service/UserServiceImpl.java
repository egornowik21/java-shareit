package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> findAll() {
        Collection<User> userDtoList = repository.findAll();
        return userDtoList
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return UserMapper.toUserDto(user);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto create(@Valid UserDto userDto) {
        User newUser = repository.save(UserMapper.inUserDto(userDto));
        return UserMapper.toUserDto(newUser);
    }
    @Override
    @Transactional(readOnly = true)
    public UserDto patch(Long id, UserDto userDto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getEmail()==null || userDto.getEmail().isBlank()) {
            user.setEmail(user.getEmail());
        }
        else {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName()==null || userDto.getName().isBlank()) {
            user.setName(user.getName());
        }
        else {
            user.setName(userDto.getName());
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (repository.findById(userId).isEmpty()) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        repository.deleteById(userId);
    }
}

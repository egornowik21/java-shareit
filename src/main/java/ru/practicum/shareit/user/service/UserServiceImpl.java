package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<UserDto> findAll() {
        return userDao.findAll();
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userId == null || !(userDao.getUsers().containsKey(userId))) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Выведен фильм по Id - {}", userId);
        return userDao.getUserbyId(userId);
    }


    @Override
    public UserDto create(@Valid User user) {
        if (userDao.getUsers().containsKey(user.getId())) {
            log.error("Добавлен существующий пользователь");
            throw new ValidationException("Пользователь c id " +
                    user.getId() + " уже зарегистрирован.");
        }
        return userDao.create(user);
    }

    @Override
    public UserDto patch(Long id, UserDto userDto) {
        if (!userDao.getUsers().containsKey(id)) {
            log.error("Пользователь с id - {} не существует", id);
            throw new NotFoundException("пользователя не существует");
        }
        log.info("Вы обновили текущего пользователя -  {}", id);
        return userDao.patch(id, userDto);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userDao.getUsers().containsKey(userId)) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователя не сущетсвует");
        }
        userDao.deleteUserById(userId);
        log.info("Вы удалили текущего пользователя - {}", userId);
    }
}

package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DublicateExeption;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Получен запрос на получение списка пользователей");
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        for (Long user : userDao.getUsers().keySet()) {
            userDtoList.add(userMapper.toUserDto(userDao.getUsers().get(user)));
        }
        return userDtoList;
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userId == null || !(userDao.getUsers().containsKey(userId))) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Выведен фильм по Id - {}", userId);
        return userMapper.toUserDto(userDao.getUsers().get(userId));
    }


    @Override
    public UserDto create(@Valid User user) {
        if (userDao.getUsers().containsKey(user.getId())) {
            log.error("Добавлен существующий пользователь");
            throw new ValidationException("Пользователь c id " +
                    user.getId() + " уже зарегистрирован.");
        } else {
            Long nextId = Long.valueOf(userDao.getUsers().size() + 1);
            user.setId(nextId);
        }
        checkMail(user);
        userDao.getUsers().put(user.getId(), user);
        log.info("Вы добавили новошго пользователя - {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto patch(Long id, UserDto userDto) {
        if (!userDao.getUsers().containsKey(id)) {
            log.error("Пользователь с id - {} не существует", id);
            throw new NotFoundException("пользователя не существует");
        }
        UserDto usetToUpdate = userMapper.toUserDto(userDao.getUserbyId(id));
        String name = userDto.getName();
        String mail = userDto.getEmail();
        if (usetToUpdate.getEmail()!=null) {
            checkMail(userMapper.inUserDto(usetToUpdate));
        }
        if (name == null || name.isEmpty() || name.isBlank()) {
            usetToUpdate.setName(usetToUpdate.getName());
        } else {
            usetToUpdate.setName(name);
        }
        if (mail == null || mail.isEmpty() || mail.isBlank()) {
            usetToUpdate.setEmail(usetToUpdate.getEmail());
        } else {
            usetToUpdate.setEmail(mail);
        }
        userDao.getUsers().put(usetToUpdate.getId(), userMapper.inUserDto(usetToUpdate));
        log.info("Вы обновили текущего пользователя -  {}", usetToUpdate.getId());
        return userMapper.toUserDto(userDao.getUsers().get(id));
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userDao.getUsers().containsKey(userId)) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователя не сущетсвует");
        }
        userDao.getUsers().remove(userId);
        log.info("Вы удалили текущего пользователя - {}", userId);
    }

    private void checkMail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new InvalidEmailException("Неверный формат почты");
        }
        for (User userchek : userDao.getUsers().values()) {
            if (user.getEmail().equals(userchek.getEmail())) {
                throw new DublicateExeption("Почта уже занята другим пользователем");
            }
        }
    }

}

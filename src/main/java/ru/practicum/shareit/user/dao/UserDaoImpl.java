package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DublicateExeption;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserMapper userMapper;
    private final Map<Long, User> users = new HashMap<>();
    private long nextId;


    @Override
    public List<UserDto> findAll() {
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        for (Long user : getUsers().keySet()) {
            userDtoList.add(userMapper.toUserDto(getUsers().get(user)));
        }
        return userDtoList;
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public UserDto getUserbyId(Long id) {
        return userMapper.toUserDto(getUsers().get(id));
    }

    @Override
    public UserDto create(@Valid User user) {
        checkMail(user);
        user.setId(++nextId);
        getUsers().put(user.getId(), user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto patch(Long id, UserDto userDto) {
        UserDto usetToUpdate = getUserbyId(id);
        String name = userDto.getName();
        String mail = userDto.getEmail();
        if (mail != null) {
            for (User userchek : getUsers().values()) {
                if (userDto.getEmail().equals(userchek.getEmail()) && getUserbyId(id).getId().equals(userchek.getId())) {
                    break;
                }
                if (userDto.getEmail().equals(userchek.getEmail())) {
                    throw new DublicateExeption("Почта уже занята другим пользователем");
                }
            }
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
        getUsers().put(usetToUpdate.getId(), userMapper.inUserDto(usetToUpdate));
        return userMapper.toUserDto(getUsers().get(id));
    }

    @Override
    public void deleteUserById(Long userId) {
        getUsers().remove(userId);
    }

    private void checkMail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new InvalidEmailException("Неверный формат почты");
        }
        for (User userchek : getUsers().values()) {
            if (user.getEmail().equals(userchek.getEmail())) {
                throw new DublicateExeption("Почта уже занята другим пользователем");
            }
        }
    }

}

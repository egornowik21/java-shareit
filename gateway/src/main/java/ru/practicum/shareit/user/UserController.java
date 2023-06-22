package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET/users - получен список всех пользователей.");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("GET/users - получен текущий пользователь.");
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST/users - добавлен текущий пользователь.");
        return userClient.postUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("DELETE/users - удален текущий пользователь.");
        userClient.deleteUser(userId);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@NotNull @PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PATCH/users - обновлен текущий пользователь.");
        return userClient.patchUser(userId, userDto);
    }


}

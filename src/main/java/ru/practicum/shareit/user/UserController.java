package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto saveNewUser(@RequestBody User user) {
        return userService.create(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("userId") Long userId) {
        return userService.patch(userId, userDto);
    }

}

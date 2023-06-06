package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    @Autowired
    RequestService requestService;
    @Autowired
    UserService userService;

    private final User user = new User(null, "user", "user@yandex.ru");
    private final User user2 = new User(null,"user2","user2@yandex.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "test", user, LocalDateTime.now());
    private final ItemRequestDto itemRequestDto = ItemRequestMapper.toRequestDto(itemRequest);


    @Test
    void create() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        ItemRequestDto testRequest = requestService.postRequestByUser(userDto.getId(),itemRequestDto);
        assertEquals(testRequest.getId(), itemRequestDto.getId());
        assertEquals(testRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void getAllRequestsByUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        requestService.postRequestByUser(userDto.getId(),itemRequestDto);
        List<ItemRequestDtoInput> requestsList = requestService.getAllRequestsByUser(userDto.getId());
        assertEquals(requestsList.size(), 1);
    }

    @Test
    void getAllRequests() {
        UserDto userDtoNotOwner = userService.create(UserMapper.toUserDto(user2));
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        requestService.postRequestByUser(userDto.getId(),itemRequestDto);
        List<ItemRequestDtoInput> requestsList = requestService.getAllRequests(userDtoNotOwner.getId(),0,10);
        assertEquals(requestsList.size(), 1);
    }

    @Test
    void getRequestById() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        ItemRequestDto testRequest = requestService.postRequestByUser(userDto.getId(),itemRequestDto);
        ItemRequestDtoInput itemRequestDtoInput = requestService.getRequestById(testRequest.getId(), userDto.getId());
        assertEquals(testRequest.getId(), itemRequestDtoInput.getId());
    }
    @Test
    void getRequestByWrongUserIdTest() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(user.getId(), 5L));
    }

    @Test
    void getRequestByOwnerWrong() {
        UserDto userDtoNotOwner = userService.create(UserMapper.toUserDto(user2));
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        requestService.postRequestByUser(userDto.getId(),itemRequestDto);
        List<ItemRequestDtoInput> requestsList = requestService.getAllRequests(userDto.getId(),0,10);
        assertEquals(requestsList.size(),0);
    }

    @Test
    void getRequestByWrongIdTest() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(5L, userDto.getId()));
    }
}

package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private ItemRequest itemRequest;
    private User user;

    private final LocalDateTime ldt = LocalDateTime.now();

    @BeforeEach
    void createItem() {
        user = new User(
                1L,
                "John",
                "john.doe@mail.com");
        itemRequest = new ItemRequest(
                1L,
                "test",
                user,
                ldt);
    }

    @Test
    void testItemDto() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toRequestDto(itemRequest);
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("John");
    }
}

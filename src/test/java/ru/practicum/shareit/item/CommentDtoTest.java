package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private Item item;
    private User user;
    private ItemRequest itemRequest;
    private Comment comment;
    private final LocalDateTime ldt = LocalDateTime.now();

    @BeforeEach
    void createItem() {
        user = new User(
                1L,
                "John7",
                "john7.doe@mail.com");
        itemRequest = new ItemRequest(
                1L,
                "test5",
                user,
                ldt);
        item = new Item(
                1L,
                "name5",
                "test5",
                TRUE,
                user,
                itemRequest);
        comment = new Comment(
                1L,
                "txt",
                user,
                item,
                ldt
        );
    }

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("txt");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John7");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String format = ldt.format(formatter);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(format);
    }

}

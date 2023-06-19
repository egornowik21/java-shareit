package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    private final User user = new User(1L, "user", "user@yandex.ru");
    private final UserDto userDto = UserMapper.toUserDto(user);
    private final ItemRequest itemRequest = new ItemRequest(1L, "test", user, LocalDateTime.now());
    private final ItemRequestDtoInput itemRequestDtoinput = ItemRequestMapper.toItemRequestDtoInput(itemRequest);
    private final Item item = new Item(1L, "item", "deskitem", Boolean.TRUE, user, itemRequest);
    private final ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
    private final ItemDto itemDto = ItemMapper.toItemDto(item);
    private final Comment comment = new Comment(1L, "comment", user, item, LocalDateTime.now());
    private final CommentDto commentDto = CommentMapper.toCommentDto(comment);
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItemTest() throws Exception {
        when(itemService.postItemByUser(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ownerId", Matchers.is(userDto.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(itemRequest.getId().intValue())));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), any()))
                .thenReturn(itemDtoWithDate);
        mockMvc.perform(get("/items/" + itemDtoWithDate.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.name", Matchers.is(userDto.getName())));
    }

    @Test
    void getListItemsByUserId() throws Exception {
        when(itemService.findItemByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoWithDate));
        mockMvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].owner.name", Matchers.is(userDto.getName())));
    }

    @Test
    void patchItemTest() throws Exception {
        when(itemService.patchItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + itemDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ownerId", Matchers.is(userDto.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(itemRequest.getId().intValue())));
    }

    @Test
    void createItemCommentTest() throws Exception {
        when(itemService.postCommentByItem(anyLong(), any(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(commentDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentDto.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName", Matchers.is(userDto.getName())));
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=" + itemDto.getName())
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ownerId", Matchers.is(userDto.getId().intValue())));
    }


    @Test
    void searchEmptyItemTest() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


}

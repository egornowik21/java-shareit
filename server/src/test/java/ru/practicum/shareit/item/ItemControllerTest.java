package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto, inputDto;

    @BeforeEach
    public void beforeEach() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        inputDto = new ItemDto();
        inputDto.setName("Item");
        inputDto.setDescription("Description");
        inputDto.setAvailable(true);
    }

    @Test
    @SneakyThrows
    public void createItemTest() {
        when(itemService.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));

        verify(itemService, times(1))
                .createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void createCommentTest() {
        CommentDto inputCommentDto = new CommentDto();
        inputCommentDto.setText("Text");
        CommentDto responseCommentDto = new CommentDto();
        responseCommentDto.setId(1L);
        responseCommentDto.setText("Text");

        when(itemService.createComment(any(Long.class), any(Long.class), any(CommentDto.class)))
                .thenReturn(responseCommentDto);


        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(inputCommentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(responseCommentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.text", is(responseCommentDto.getText()), String.class));

        verify(itemService, times(1))
                .createComment(any(Long.class), any(Long.class), any(CommentDto.class));
    }

    @Test
    @SneakyThrows
    public void updateTest() {
        inputDto.setName("new name");
        itemDto.setName("new name");

        when(itemService.updateItem(any(ItemDto.class), any(Long.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));

        verify(itemService, times(1))
                .updateItem(any(ItemDto.class), any(Long.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void findByIdTest() {
        when(itemService.getItem(any(Long.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));

        verify(itemService, times(1)).getItem(any(Long.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void getAllTest() {
        when(itemService.getAllItems(any(Long.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .getAllItems(any(Long.class), any(Pageable.class));
    }

    @Test
    @SneakyThrows
    public void findByRequestTest() throws Exception {
        when(itemService.searchItems(any(String.class), any()))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "any text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .searchItems(any(String.class), any());
    }
}

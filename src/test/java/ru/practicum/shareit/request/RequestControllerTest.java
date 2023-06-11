package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @MockBean
    RequestService requestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final User user = new User(1L, "user", "user@yandex.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "test", user, LocalDateTime.now());
    private final ItemRequest itemRequest2 = new ItemRequest(1L, null, user, LocalDateTime.now());
    private final ItemRequestDtoInput itemRequestDtoinput = ItemRequestMapper.toItemRequestDtoInput(itemRequest);
    private final ItemRequestDto itemRequestDto = ItemRequestMapper.toRequestDto(itemRequest);
    private final ItemRequestDto itemRequestDto2 = ItemRequestMapper.toRequestDto(itemRequest2);
    private final UserDto userDto = UserMapper.toUserDto(user);

    @Test
    void createRequestTest() throws Exception {
        when(requestService.postRequestByUser(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemRequest.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemRequest.getDescription())));
    }

    @Test
    void createFaildRequestTest() throws Exception {
        when(requestService.postRequestByUser(anyLong(), any()))
                .thenReturn(itemRequestDto2);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsTest() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoinput));

        mockMvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDtoinput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsByIdTest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoinput);

        mockMvc.perform(get("/requests/" + itemRequestDtoinput.getId())
                        .content(objectMapper.writeValueAsString(itemRequestDtoinput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemRequestDtoinput.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemRequestDtoinput.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestorId", Matchers.is(userDto.getId().intValue())));
        verify(requestService, times(1))
                .getRequestById(anyLong(), anyLong());
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoinput));

        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .content(objectMapper.writeValueAsString(itemRequestDtoinput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getFaildAllRequestsTest() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoinput));

        mockMvc.perform(get("/requests/all?from=&size=")
                        .content(objectMapper.writeValueAsString(itemRequestDtoinput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk());
    }


}

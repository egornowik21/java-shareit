package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    private void beforeEach() {

        user = new User(1L, "Name1", "Name1@mail.ru");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime
                .parse("2023-05-03T10:34:35.15"));
    }

    @Test
    public void createTest() {
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setId(1L);
        inputDto.setDescription(itemRequest.getDescription());

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto responseDto = itemRequestService.createItemRequest(1L, inputDto);

        assertEquals(1L, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    void getAllByUserTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);

        when(itemRequestRepository
                .findAllByRequesterIdOrderByCreatedAsc(any(Long.class)))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> result = itemRequestService.findALLItemRequestByUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /*@Test
    void getAllTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.findAllByRequesterNotLikeOrderByCreatedAsc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestByUser(Pageable.ofSize(0, 10, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }*/

    @Test
    void getByIdTest() {
        when(userRepository.existsById(any(Long.class)))
                .thenReturn(true);

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(itemRepository.findAllByRequestId(any(Long.class)))
                .thenReturn(new ArrayList<>());


        ItemRequestDto result = itemRequestService.getItemRequestById(1L, 1L);

        assertEquals(1L, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}
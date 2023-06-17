package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    private void beforeEach() {
        itemRequest = new ItemRequest(1L, "Description", null, null);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
    }

    @Test
    public void toItemRequestDtoTest() {
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void toItemRequestTest() {
        ItemRequest newItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(newItemRequest.getId(), itemRequestDto.getId());
        assertEquals(newItemRequest.getDescription(), itemRequestDto.getDescription());
    }
}

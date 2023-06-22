package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import java.util.List;

public interface RequestService {
    ItemRequestDto postRequestByUser(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoInput> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoInput getRequestById(Long itemId, Long userId);

    public List<ItemRequestDtoInput> getAllRequestsByUser(Long userId);
}

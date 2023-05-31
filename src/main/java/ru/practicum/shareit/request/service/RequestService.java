package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import javax.validation.Valid;
import java.util.List;

public interface RequestService {
    ItemRequestDto postRequestByUser(@Valid Long userId, @Valid ItemRequestDto itemRequestDto);
    List<ItemRequestDtoInput> getAllRequests(Long userId, Integer from, Integer size);
    ItemRequestDto getRequestById(Long itemId, Long userId);
    public List<ItemRequestDtoInput> getAllRequestsByUser(Long userId);
}

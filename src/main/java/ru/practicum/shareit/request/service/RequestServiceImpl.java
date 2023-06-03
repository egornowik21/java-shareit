package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto postRequestByUser(@Valid Long userId, @Valid ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.inRequestDto(itemRequestDto, user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoInput> getAllRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from, size, Sort.by("created"));
        List<Item> items = itemRepository.findAll();
        List<ItemRequestDtoInput> itemRequestDto = requestRepository.findByRequestor_IdNot(userId,pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoInput)
                .collect(Collectors.toList());
        return searchItemsByRequest(itemRequestDto, items);
    }

    @Override
    public List<ItemRequestDtoInput> getAllRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Item> items = itemRepository.findAll();
        List<ItemRequestDtoInput> itemRequestDto = requestRepository.findByRequestorIdOrderByIdAsc(user.getId())
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoInput)
                .collect(Collectors.toList());
        return searchItemsByRequest(itemRequestDto, items);
    }

    @Override
    public ItemRequestDtoInput getRequestById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<ItemDto> itemsListIds = itemRepository.findByRequestIdOrderByIdAsc(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDtoInput itemRequestDtotoReturn = ItemRequestMapper.toItemRequestDtoInput(itemRequest);
        itemRequestDtotoReturn.setItems(itemsListIds);
        return itemRequestDtotoReturn;
    }

    private List<ItemRequestDtoInput> searchItemsByRequest(List<ItemRequestDtoInput> requestDtos, List<Item> items) {
        for (ItemRequestDtoInput request : requestDtos) {
            List<ItemDto> answers = new ArrayList<>();
            for (Item item : items) {
                if (item.getRequest() != null) {
                    if (item.getRequest().getId().equals(request.getId())) {
                        answers.add(ItemMapper.toItemDto(item));
                    }
                }
                request.setItems(answers);
            }
        }
        return requestDtos;
    }
}

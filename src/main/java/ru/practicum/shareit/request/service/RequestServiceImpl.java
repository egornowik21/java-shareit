package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
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
        List<Item> itemsList = itemRepository.findByOwnerIdOrderByIdAsc(user.getId());
        return requestRepository.findAll(pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoInput)
                .peek(x -> x.setItems(itemsList))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoInput> getAllRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Item> itemsList = itemRepository.findByOwnerIdOrderByIdAsc(user.getId());
        return requestRepository.findByRequestorIdOrderByIdAsc(user.getId())
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoInput)
                .peek(x -> x.setItems(itemsList))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Pfghjc не найден"));
        return ItemRequestMapper.toRequestDto(itemRequest);
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("запрос не может быть пустым");
        }
        log.info("POST/request - получение запрос от пользователя с ID - {}.", userId);
        return requestService.postRequestByUser(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoInput> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET/requests - получен список всех запросов от пользователя с ID - {}.", userId);
        return requestService.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInput> findAllRequestsByOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(value = "from", required = false) Integer from,
                                                            @RequestParam(value = "size", required = false) Integer size) {
        log.info("GET/requests - получен список всех запросов от пользователя с ID - {}.", userId);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("GET/request - получен запрос по ID - {}.", requestId);
        return requestService.getRequestById(requestId, userId);
    }

}

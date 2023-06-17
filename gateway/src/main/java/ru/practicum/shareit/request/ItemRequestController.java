package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("запрос не может быть пустым");
        }
        log.info("POST/request - получение запрос от пользователя с ID - {}.", userId);
        return requestClient.postRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET/requests - получен список всех запросов от пользователя с ID - {}.", userId);
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsByOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                                              @RequestParam(value = "size", required = false) Integer size) {
        if (from == null || size == null) {
            return (ResponseEntity<Object>) Collections.emptyList();
        }
        log.info("GET/requests - получен список всех запросов от пользователя с ID - {}.", userId);
        return requestClient.getRequestsByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("GET/request - получен запрос по ID - {}.", requestId);
        return requestClient.getRequestById(requestId, userId);
    }

}

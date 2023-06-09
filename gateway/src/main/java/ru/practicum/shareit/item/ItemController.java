package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable long itemId) {
        log.info("GET/items - получен список всех вещей.");
        return itemClient.getItems(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET/items - получен список всех вещей по ID пользователя - {}.", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemDto itemDto) {
        log.info("POST/items - добавлена вещь для ID пользователя - {}.", userId);
        return itemClient.postItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(commentDto, itemId, userId);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemDto itemDto,
                                            @PathVariable("itemId") Long itemId) {
        log.info("PATCH/items - обновлена вещь для ID пользователя - {}.", userId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET/items - выполнен поиск вещи.");
        if (text == null || text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.searchItem(userId, text, from, size);
    }

}

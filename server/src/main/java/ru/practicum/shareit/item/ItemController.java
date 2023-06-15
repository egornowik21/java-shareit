package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoWithDate getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable long itemId) {
        log.info("GET/items - получен список всех вещей.");
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithDate> findItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET/items - получен список всех вещей по ID пользователя - {}.", userId);
        return itemService.findItemByUserId(userId, from, size);
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody ItemDto itemDto) {
        log.info("POST/items - добавлена вещь для ID пользователя - {}.", userId);
        return itemService.postItemByUser(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postCommentByItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody CommentDto commentDto,
                                        @PathVariable("itemId") Long itemId) {
        log.info("POST/comments - добавлен комментарий от пользователя - {}.", userId);
        return itemService.postCommentByItem(userId, commentDto, itemId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody Item item,
                             @PathVariable("itemId") Long itemId) {
        log.info("PATCH/items - обновлена вещь для ID пользователя - {}.", userId);
        return itemService.patchItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        log.info("GET/items - выполнен поиск вещи.");
        return itemService.searchItem(text, from, size);
    }

}

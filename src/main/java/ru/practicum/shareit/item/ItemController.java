package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getAllItemsByUser(@PathVariable long itemId) {
        log.info("GET/items - получен список всех вещей.");
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> findItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET/items - получен список всех вещей по ID пользователя - {}.", userId);
        return itemService.findItemByUserId(userId);
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody ItemDto itemDto) {
        log.info("POST/items - добавлена вещь для ID пользователя - {}.", userId);
        return itemService.postItemByUser(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody Item item,
                             @PathVariable("itemId") Long itemId) {
        log.info("PATCH/items - обновлена вещь для ID пользователя - {}.", userId);
        return itemService.patchItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        log.info("GET/items - выполнен поиск вещи.");
        return itemService.searchItem(text);
    }

}

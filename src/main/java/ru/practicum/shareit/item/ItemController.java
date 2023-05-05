package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getAllItemsByUser(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> findItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findItemByUserId(userId);
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody Item item) {
        return itemService.postItemByUser(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody Item item,
                             @PathVariable("itemId") Long itemId) {
        return itemService.patchItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(value = "text", required = false) String query) {
        return itemService.searchItem(query);
    }

}

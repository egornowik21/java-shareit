package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemDao {
    List<ItemDto> findItemByUserId(Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems();

    Map<Long, Item> getItemsMap();

    ItemDto postItemByUser(Long userId, Item item);

    ItemDto patchItem(Long userId, Item item, Long itemId);

    List<ItemDto> searchItem(String text);
}

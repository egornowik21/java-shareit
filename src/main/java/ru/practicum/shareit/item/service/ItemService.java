package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> findItemByUserId(Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems();

    ItemDto postItemByUser(Long userId, ItemDto itemDto);

    ItemDto patchItem(Long userId, Item item, Long itemId);

    List<ItemDto> searchItem(String text);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDtoWithDate> findItemByUserId(Long userId,Integer from, Integer size);

    ItemDtoWithDate getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItems();

    ItemDto postItemByUser(Long userId, ItemDto itemDto);

    ItemDto patchItem(Long userId, Item item, Long itemId);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    CommentDto postCommentByItem(Long userId, CommentDto commentDto, Long itemId);
}

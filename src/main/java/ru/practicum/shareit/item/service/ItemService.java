package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    List<ItemDtoWithDate> findItemByUserId(Long userId);

    ItemDtoWithDate getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItems();

    ItemDto postItemByUser(Long userId, ItemDto itemDto);

    ItemDto patchItem(Long userId, Item item, Long itemId);

    List<ItemDto> searchItem(String text);

    CommentDto postCommentByItem(@Valid Long userId, @Valid CommentDto commentDto, Long itemId);
}

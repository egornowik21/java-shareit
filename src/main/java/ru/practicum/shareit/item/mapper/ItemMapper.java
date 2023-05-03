package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    private final Item item;

    public ItemMapper(Item item) {
        this.item = item;
    }

    /*public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest()!=null?item.getRequest().getId() : null
        );*/
}

package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemRequestDtoInput {
    Long id;
    String description;
    Long requestorId;
    List<ItemDto> items;
    LocalDateTime created;
}

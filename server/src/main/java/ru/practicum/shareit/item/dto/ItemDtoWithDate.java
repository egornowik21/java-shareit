package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemDtoWithDate {
    long id;
    String name;
    String description;
    Boolean available;
    User owner;
    BookingDtoInput lastBooking;
    BookingDtoInput nextBooking;
    List<CommentDto> comments;
}

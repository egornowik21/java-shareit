package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemDtoWithDate {
    long id;
    @NotBlank(message = "Имя не может быть пустым")
    @NotNull(message = "Имя не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    @NotNull(message = "Описание не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    String description;
    @NotBlank
    Boolean available;
    User owner;
    BookingDtoInput lastBooking;
    BookingDtoInput nextBooking;
    List<CommentDto> comments;
}

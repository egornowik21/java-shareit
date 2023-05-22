package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 *
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class BookingDtoInput {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long booker_id;
    Status status;
}

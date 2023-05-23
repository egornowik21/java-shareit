package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDtoInput bookingDtoInputId(Booking booking) {
        return BookingDtoInput.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
    public static Booking bookingDtoEntity(BookingDtoInput bookingDtoInput, User user, Item item) {
        return Booking.builder()
                .id(bookingDtoInput.getId())
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .item(item)
                .booker(user)
                .build();
    }
}

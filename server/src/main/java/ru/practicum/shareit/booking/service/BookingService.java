package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.util.List;

public interface BookingService {
    BookingDto postBookingByUser(Long userId, BookingDtoInput bookingDtoInput);

    BookingDto patchBookingByUser(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBokingsByUser(String state, Long userId, Integer from, Integer size);

    List<BookingDto> getAllBokingsByOwner(String state, Long userId, Integer from, Integer size);
}

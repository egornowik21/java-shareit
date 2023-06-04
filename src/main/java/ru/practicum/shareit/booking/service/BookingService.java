package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import javax.validation.Valid;
import java.util.List;

public interface BookingService {
    BookingDto postBookingByUser(@Valid Long userId, @Valid BookingDtoInput bookingDtoInput);

    BookingDto patchBookingByUser(@Valid Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBokingsByUser(String state, Long userId, Integer from, Integer size);

    List<BookingDto> getAllBokingsByOwner(String state, Long userId,Integer from, Integer size);
}

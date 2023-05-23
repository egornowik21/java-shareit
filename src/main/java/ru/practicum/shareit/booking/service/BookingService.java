package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import java.util.List;

public interface BookingService {
    Booking postBookingByUser(@Valid Long userId, @Valid BookingDtoInput bookingDtoInput);

    Booking patchBookingByUser(@Valid Long userId, Long bookingId, Boolean approved);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getAllBokingsByUser(String state, Long userId);

    List<Booking> getAllBokingsByOwner(String state, Long userId);
}

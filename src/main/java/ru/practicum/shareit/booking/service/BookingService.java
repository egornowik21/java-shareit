package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;
import javax.validation.Valid;

public interface BookingService {
    Booking postBookingByUser(@Valid Long userId, @Valid BookingDtoInput bookingDtoInput);
    Booking patchBookingByUser(@Valid Long userId, Long bookingId, Boolean approved);
    Booking getBookingById(Long bookingId, Long userId);
    List<Booking> getAllBokingsByUser(String state, Long userId);
    List<Booking> getAllBokingsByOwner(String state, Long userId);
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody BookingDtoInput bookingDtoInput) {
        log.info("POST/bookings - добавлено новое бронирование для ID пользователя - {}.", userId);
        return bookingService.postBookingByUser(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public Booking patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam(value = "approved", required = false) Boolean approved,
                             @PathVariable("bookingId") Long bookingId) {
        log.info("PATCH/bookings - бронирование обновлено для ID пользователя - {}.", userId);
        return bookingService.patchBookingByUser(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("bookingId") Long bookingId) {
        log.info("GET/bookings - получено бронирование по ID - {}.", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getAllBookingsByUserID(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state", required = false) String state) {
        log.info("GET/bookings - получено бронирование для пользователя по ID - {}.", userId);
        return bookingService.getAllBokingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsByOwnerID(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "state", required = false) String state) {
        log.info("GET/bookings - получено бронирование для владельца по ID - {}.", userId);
        return bookingService.getAllBokingsByOwner(state, userId);
    }
}

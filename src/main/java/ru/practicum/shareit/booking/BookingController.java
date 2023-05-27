package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody BookingDtoInput bookingDtoInput) {
        log.info("POST/bookings - добавлено новое бронирование для ID пользователя - {}.", userId);
        return bookingService.postBookingByUser(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(value = "approved") boolean approved,
                                @PathVariable("bookingId") Long bookingId) {
        log.info("PATCH/bookings - бронирование обновлено для ID пользователя - {}.", userId);
        return bookingService.patchBookingByUser(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("bookingId") Long bookingId) {
        log.info("GET/bookings - получено бронирование по ID - {}.", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserID(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET/bookings - получено бронирование для пользователя по ID - {}.", userId);
        State status = State.fromString(state);
        if (status == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingService.getAllBokingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwnerID(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET/bookings - получено бронирование для владельца по ID - {}.", userId);
        State status = State.fromString(state);
        if (status == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingService.getAllBokingsByOwner(status.toString(), userId);
    }
}

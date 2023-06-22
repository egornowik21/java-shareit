package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserID(@Validated @RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                         @PositiveOrZero @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET/bookings - получено бронирование для пользователя по ID - {}.", userId);
        State status = State.fromString(state);
        if (status == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        if (from < 0 || size < 0) {
            throw new ValidationException("Введены отрицательные значения");
        }
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid BookingDtoInput bookingDtoInput) {
        log.info("POST/bookings - добавлено новое бронирование для ID пользователя - {}.", userId);
        return bookingClient.bookItem(userId, bookingDtoInput);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("bookingId") Long bookingId) {
        log.info("GET/bookings - получено бронирование по ID - {}.", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(value = "approved") boolean approved,
                                               @PathVariable("bookingId") Long bookingId) {
        log.info("PATCH/bookings - бронирование обновлено для ID пользователя - {}.", userId);
        return bookingClient.patchBooking(userId, bookingId, approved);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State status = State.fromString(stateParam);
        if (status == null) {
            throw new ValidationException("Unknown state: " + stateParam);
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, stateParam, from, size);
    }

}

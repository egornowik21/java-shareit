package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.constraints.PositiveOrZero;
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
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
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
    public List<BookingDto> getAllBookingsByUserID(@Validated @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @PositiveOrZero @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET/bookings - получено бронирование для пользователя по ID - {}.", userId);
        State status = State.fromString(state);
        if (status == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        if (from<0||size<0) {
            throw new ValidationException("Введены отрицательные значения");
        }
        return bookingService.getAllBokingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwnerID(@Validated @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @PositiveOrZero @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET/bookings - получено бронирование для владельца по ID - {}.", userId);
        State status = State.fromString(state);
        if (status == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        if (from<0||size<0) {
            throw new ValidationException("Введены отрицательные значения");
        }
        return bookingService.getAllBokingsByOwner(status.toString(), userId, from, size);
    }
}

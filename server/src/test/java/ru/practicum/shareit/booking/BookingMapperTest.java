package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private Booking booking;
    private BookingShortDto bookingShortDto;

    @BeforeEach
    public void beforeEach() {
        booking = new Booking(1L,
                LocalDateTime.parse("2023-05-03T10:34:35.15"),
                LocalDateTime.parse("2023-05-02T19:34:35.15"),
                new Item(), new User(), null);

        bookingShortDto = new BookingShortDto();
        bookingShortDto.setId(1L);
        bookingShortDto.setStart(LocalDateTime.parse("2023-05-03T10:34:35.15"));
        bookingShortDto.setEnd(LocalDateTime.parse("2023-05-02T19:34:35.15"));
    }

    @Test
    public void toBookingDtoTest() {
        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void bookingBriefDtoTest() {
        BookingShortDto dto = BookingMapper.toBookingShortDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void toBookingTest() {
        Booking newBooking = BookingMapper.toBooking(bookingShortDto);

        assertEquals(newBooking.getStart(), bookingShortDto.getStart());
        assertEquals(newBooking.getEnd(), bookingShortDto.getEnd());
    }
}

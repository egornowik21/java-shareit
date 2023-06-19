package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private Item item;
    private User user;
    private ItemRequest itemRequest;

    private Booking booking;
    private final LocalDateTime ldt = LocalDateTime.now();

    @BeforeEach
    void createBooking() {
        user = new User(
                1L,
                "John",
                "john.doe@mail.com");
        itemRequest = new ItemRequest(
                1L,
                "test",
                user,
                ldt);
        item = new Item(
                1L,
                "name",
                "test",
                TRUE,
                user,
                itemRequest);
        booking = new Booking(
                1L,
                ldt.minusDays(1),
                ldt.plusDays(1),
                item,
                user,
                Status.APPROVED
        );
    }

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = BookingMapper.bookingDto(booking);
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String format2 = ldt.minusDays(1).format(formatter);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(format2);
        String format = ldt.plusDays(1).format(formatter);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(format);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John");
    }
}

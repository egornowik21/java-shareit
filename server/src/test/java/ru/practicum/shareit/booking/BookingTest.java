package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingTest {
    @Autowired
    JacksonTester<BookingShortDto> json;

    @Test
    @SneakyThrows
    void bookingBriefDtoJsonTest() {
        BookingShortDto bookingBriefDto = new BookingShortDto();
        bookingBriefDto.setId(1L);
        bookingBriefDto.setStart(LocalDateTime.parse("2023-05-03T10:34:35.15"));
        bookingBriefDto.setEnd(LocalDateTime.parse("2023-05-02T19:34:35.15"));
        JsonContent<BookingShortDto> result = json.write(bookingBriefDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-05-03T10:34:35.15");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-05-02T19:34:35.15");
    }
}

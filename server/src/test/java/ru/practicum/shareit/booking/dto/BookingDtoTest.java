package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Autowired
    ObjectMapper mapper;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime startBooking = LocalDateTime.now().plusDays(7);
        LocalDateTime endBooking = LocalDateTime.now().plusDays(10);
        //сериализуем дату старта последнего бронирования
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String startBookingString = mapper.writeValueAsString(startBooking);
        String endBookingString = mapper.writeValueAsString(endBooking);

        BookingDto bookingDto = new BookingDto(1L,startBooking, endBooking, null, null, Status.APPROVED);
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(startBookingString.replace("\"", ""));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(endBookingString.replace("\"", ""));

    }

}
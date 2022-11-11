package ru.practicum.shareit.booking.mapper;


import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingDto.ItemInput(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new BookingDto.UserInput(booking.getBooker().getId()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingInputDto bookingInputDto) {
        return Booking.builder()
                .id(bookingInputDto.getId())
                .start(bookingInputDto.getStart())
                .end(bookingInputDto.getEnd())
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .isApproved(false)
                .build();
    }
}

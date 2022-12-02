package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;


import java.util.List;

public interface BookingService {

    BookingDto create(BookingInputDto bookingInputDto, Long bookerId);

    List<BookingDto> getAllByBooker(Long userId, String state, int from, int size);

    List<BookingDto> getAllByOwner(Long userId, String state, int from, int size);

    BookingDto getById(Long id, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

}

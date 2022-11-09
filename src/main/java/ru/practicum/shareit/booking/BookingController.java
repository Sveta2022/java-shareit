package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.request.exception.NoligalArgumentException;

import javax.validation.Valid;
import java.util.List;

/**
 * add-bookings
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@Valid
                             @RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Запрос на добавление бронирования с id " + bookingInputDto.getId() + " создан");
        return service.create(bookingInputDto, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllforBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Запрос на получить все бронирования для арендатора создан");
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new NoligalArgumentException("Unknown state: " + state);
        }
        return service.getAllByBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllforOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", defaultValue = "ALL") String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new NoligalArgumentException("Unknown state: " + state);
        }

        log.info("Запрос на получить все бронирования для арендодателя создан");
        return service.getAllByOwner(userId, bookingState);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение бронирования по id " + bookingId + " создан");
        return service.getById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam(defaultValue = "false") Boolean approved) {
        log.info("Запрос на обновление бронирование с id " + bookingId + " создан");
        return service.update(bookingId, userId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Long bookingId) {
        log.info("Запрос на удаление бронирования с id " + bookingId + " создан");
        service.delete(bookingId);
    }
}

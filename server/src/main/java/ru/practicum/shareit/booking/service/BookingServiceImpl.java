package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.validation.exception.NotFoundObjectException;
import ru.practicum.shareit.validation.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    BookingStorage storage;
    ItemStorage itemStorage;
    UserStorage userStorage;

    @Autowired
    public BookingServiceImpl(BookingStorage storage, ItemStorage itemStorage, UserStorage userStorage) {
        this.storage = storage;
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    @Transactional
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) {
        User userBooker = userStorage.findById(bookerId)
                .orElseThrow(() -> new NotFoundObjectException("Пользователя с id " + bookerId + " не зарегестрирован"));
        validBookingDate(bookingInputDto);
        if (bookingInputDto.getItemId() == null) {
            throw new ValidationException("Укажите предмет для бронирования");
        }
        Long itemId = bookingInputDto.getItemId();
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundObjectException("Вещь с id " + itemId + " не найдена"));
        User ownerUser = item.getOwner();
        if (ownerUser.equals(userBooker)) {
            throw new NotFoundObjectException("Владелец вещи не может забронировать собственную вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("вещь с id " + itemId + " не доступна для бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingInputDto);
        booking.setBooker(userBooker);
        booking.setItem(item);
        return BookingMapper.toDto(storage.save(booking));
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state, int from, int size) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Нет такого пользователя"));
        if (from < 0 || size <= 0) {
            throw new ValidationException("не верно указан количество позиций на странице");
        }
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = storage.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = storage.findAllByBookerCurrent(userId, LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = storage.findAllByBookerIdAndEndBeforeOrderByIdDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = storage.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = storage.findAllByBookerIdState(userId, Status.WAITING.toString(), pageRequest);
                break;
            case "REJECTED":
                bookings = storage.findAllByBookerIdState(userId, Status.REJECTED.toString(), pageRequest);
                break;
        }
        if (bookings != null) {
            return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
        }
        throw new NotFoundObjectException("В базе нет такого объекта");
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state, int from, int size) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Нет такого пользователя"));
        if (from < 0 || size <= 0) {
            throw new ValidationException("не верно указан количество позиций на странице");
        }
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = storage.findAllByOwnerIdOrderByStartDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = storage.findAllByOwnerIdCurrent(userId, LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = storage.findAllByOwnerIdStatePast(userId, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = storage.findAllByOwnerIdStateFuture(userId, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = storage.findAllByOwnerIdState(userId, Status.WAITING.toString(), pageRequest);
                break;
            case "REJECTED":
                bookings = storage.findAllByOwnerIdState(userId, Status.REJECTED.toString(), pageRequest);
                break;
        }
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto getById(Long id, Long userId) {
        Booking booking = storage.findById(id)
                .orElseThrow(() -> new NotFoundObjectException("нет такого объекта"));
        User bookerUser = booking.getBooker();
        User ownerUser = booking.getItem().getOwner();
        if (!bookerUser.getId().equals(userId) && !ownerUser.getId().equals(userId)) {
            throw new NotFoundObjectException("у пользователя нет такого бронирования");
        }
        User user = userStorage.findById(userId).orElseThrow(() -> new ValidationException("нет такого объекта"));
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = storage.findById(bookingId)
                .orElseThrow(() -> new NotFoundObjectException("нет такого объекта"));
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundObjectException("нет такого объекта"));
        User bookerUser = booking.getBooker();
        Item item = itemStorage.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundObjectException("нет такого объекта"));
        User ownerUser = item.getOwner();
        if (!Objects.equals(bookerUser.getId(), userId)) {
            if (Objects.equals(ownerUser.getId(), userId)) {
                if (booking.getStatus() == Status.APPROVED) {
                    throw new ValidationException("Статус APPROVED уже установлен");
                }
                booking.setIsApproved(approved);
                if (booking.getIsApproved()) {
                    booking.setStatus(Status.APPROVED);
                } else {
                    booking.setStatus(Status.REJECTED);
                }
            } else {
                throw new ValidationException("Изменения может вносить только пользователь");
            }
        } else {
            throw new NotFoundObjectException("Арендатор не может устанавливать статус");
        }
        return BookingMapper.toDto(storage.save(booking));
    }

    private void validBookingDate(BookingInputDto bookingInputDto) {
        LocalDateTime start = bookingInputDto.getStart();
        LocalDateTime end = bookingInputDto.getEnd();
        if (start.isAfter(end) || end.isBefore(start)) {
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания бронирования");
        }
    }
}


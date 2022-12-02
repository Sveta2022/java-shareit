package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.NotFoundObjectException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {
    @Mock
    UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private BookingStorage bookingStorage;

    ItemService itemService;
    UserService userService;
    BookingService bookingService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;
    private Item item1;
    private BookingInputDto bookingInputDto;
    private BookingDto bookingDtoNew;

    @Autowired
    public BookingServiceImplTest(UserStorage userStorage, ItemStorage itemStorage,
                                  BookingStorage bookingStorage, ItemService itemService,
                                  UserService userService, BookingService bookingService) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.bookingStorage = bookingStorage;
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @BeforeEach
    void start() {
        LocalDateTime startLastBooking = LocalDateTime.now().minusDays(7);
        LocalDateTime endLastBooking = LocalDateTime.now().minusDays(3);
        LocalDateTime startNextBooking = LocalDateTime.now().plusDays(3);
        LocalDateTime endNextBooking = LocalDateTime.now().plusDays(7);
        ItemDto.BookingDtoItem lastBooking = new ItemDto.BookingDtoItem(2L, startLastBooking,
                endLastBooking, 1L);
        ItemDto.BookingDtoItem nextBooking = new ItemDto.BookingDtoItem(3L, startNextBooking,
                endNextBooking, 2L);

        userDto1 = new UserDto(1L, "userDtoname1", "userDto1Email@email");
        userDto2 = new UserDto(2L, "userDtoname2", "userDto2Email@email");

        user1 = UserMapper.toUser(userDto1);
        user2 = UserMapper.toUser(userDto2);

        itemDto1 = new ItemDto(1L, "itemName", "itemDescription", true, lastBooking,
                nextBooking, null, null);
        item1 = ItemMapper.toItem(itemDto1);

        bookingInputDto = new BookingInputDto(1L, item1.getId(), LocalDateTime.now().plusDays(9),
                LocalDateTime.now().plusDays(15));

        userStorage.save(user1);
        userStorage.save(user2);
        itemStorage.save(item1);
        userService.create(userDto1);
        userService.create(userDto2);
        itemService.create(user1.getId(), itemDto1);
        bookingDtoNew = bookingService.create(bookingInputDto, user2.getId());
    }

    @Test
    void create() {
        assertEquals(bookingDtoNew.getId(), 1L);
    }

    @Test
    void createWithWrongItem() {
        BookingInputDto bookingInputDto2 = new BookingInputDto(2L,
                null, LocalDateTime.now().plusDays(25), LocalDateTime.now().plusDays(35));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(bookingInputDto2, user2.getId()));
        assertEquals("Укажите предмет для бронирования", exception.getMessage());
    }

    @Test
    void createWithWrongBookerId() {
        BookingInputDto bookingInputDto2 = new BookingInputDto(2L,
                item1.getId(), LocalDateTime.now().plusDays(25), LocalDateTime.now().plusDays(35));
        final NotFoundObjectException exception = assertThrows(NotFoundObjectException.class,
                () -> bookingService.create(bookingInputDto2, user1.getId()));
        assertEquals("Владелец вещи не может забронировать собственную вещь", exception.getMessage());
    }

    @Test
    void createWithItemNotAvailable() {
        item1.setAvailable(false);
        itemDto1.setAvailable(false);
        itemStorage.save(item1);
        itemService.create(user1.getId(), itemDto1);
        BookingInputDto bookingInputDto2 = new BookingInputDto(2L,
                item1.getId(), LocalDateTime.now().plusDays(25), LocalDateTime.now().plusDays(35));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(bookingInputDto2, user2.getId()));
        assertEquals(("вещь с id " + itemDto1.getId() + " не доступна для бронирования"), exception.getMessage());
    }

    @Test
    void getAllByBooker() {
        List<BookingDto> allByBooker = bookingService.getAllByBooker(user2.getId(), "ALL", 1, 10);
        assertEquals(allByBooker.size(), 1);
    }

    @Test
    void getAllByBookerWithWrongPage() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllByBooker(user2.getId(), "ALL", -1, -10));

        assertEquals("не верно указан количество позиций на странице", exception.getMessage());
    }

    @Test
    void getAllByBookerCurrent() {
        List<BookingDto> allByBooker = bookingService.getAllByBooker(user2.getId(), "CURRENT", 1, 10);
        assertEquals(allByBooker.size(), 0);
    }

    @Test
    void getAllByBookerPast() {
        List<BookingDto> allByBooker = bookingService.getAllByBooker(user2.getId(), "PAST", 1, 10);
        assertEquals(allByBooker.size(), 0);
    }

    @Test
    void getAllByBookerFuture() {
        List<BookingDto> allByBooker = bookingService.getAllByBooker(user2.getId(), "FUTURE", 1, 10);
        assertEquals(allByBooker.size(), 1);
    }

    @Test
    void getAllByBookerWaiting() {
        List<BookingDto> allByBooker = bookingService.getAllByBooker(user2.getId(), "WAITING", 1, 10);
        assertEquals(allByBooker.size(), 1);
    }

    @Test
    void getAllByBookerRejected() {
        List<BookingDto> allByBooker = bookingService.getAllByBooker(user2.getId(), "REJECTED", 1, 10);
        assertEquals(allByBooker.size(), 0);
    }

    @Test
    void getAllByOwner() {
        List<BookingDto> allByBooker = bookingService.getAllByOwner(user1.getId(), "ALL", 1, 10);
        assertEquals(allByBooker.size(), 1);
    }

    @Test
    void getAllByOwnerWrongPage() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllByOwner(user1.getId(), "ALL", -1, 10));
        assertEquals("не верно указан количество позиций на странице", exception.getMessage());
    }

    @Test
    void getAllByOwnerCurrent() {
        List<BookingDto> allByBooker = bookingService.getAllByOwner(user1.getId(), "CURRENT", 1, 10);
        assertEquals(allByBooker.size(), 0);
    }

    @Test
    void getAllByOwnerPast() {
        List<BookingDto> allByBooker = bookingService.getAllByOwner(user1.getId(), "PAST", 1, 10);
        assertEquals(allByBooker.size(), 0);
    }

    @Test
    void getAllByOwnerWaiting() {
        List<BookingDto> allByBooker = bookingService.getAllByOwner(user1.getId(), "WAITING", 1, 10);
        assertEquals(allByBooker.size(), 1);
    }

    @Test
    void getAllByOwnerReject() {
        List<BookingDto> allByBooker = bookingService.getAllByOwner(user1.getId(), "REJECTED", 1, 10);
        assertEquals(allByBooker.size(), 0);
    }

    @Test
    void getById() {
        BookingDto bookingDtoGetById = bookingService.getById(bookingInputDto.getId(), user2.getId());
        assertEquals(bookingDtoGetById.getId(), bookingInputDto.getId());
    }

    @Test
    void update() {
        BookingDto bookingDtoUpdate = bookingService.update(bookingDtoNew.getId(), user1.getId(), true);
        assertEquals(bookingDtoUpdate.getStatus(), Status.APPROVED);
    }
}
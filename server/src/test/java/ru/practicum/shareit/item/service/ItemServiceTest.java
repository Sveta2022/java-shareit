package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestStorage;
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
class ItemServiceTest {

    @Mock
    UserStorage userStorage;
    @Mock
    private ItemStorage storage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private RequestStorage requestStorage;

    private ItemService itemService;
    private UserService userService;
    private BookingService bookingService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;

    private Item item1;

    private ItemDto itemDtoNew;
    private BookingDto bookingDto1;
    private CommentDto commentDto1;
    private Comment comment1;

    @Autowired
    public ItemServiceTest(UserStorage userStorage, ItemStorage storage,
                           CommentStorage commentStorage, BookingStorage bookingStorage,
                           RequestStorage requestStorage, ItemService itemService,
                           UserService userService, BookingService bookingService) {
        this.userStorage = userStorage;
        this.storage = storage;
        this.commentStorage = commentStorage;
        this.bookingStorage = bookingStorage;
        this.requestStorage = requestStorage;
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
        ItemDto.BookingDtoItem lastBooking = new ItemDto.BookingDtoItem(1L, startLastBooking, endLastBooking, 1L);
        ItemDto.BookingDtoItem nextBooking = new ItemDto.BookingDtoItem(2L, startNextBooking, endNextBooking, 2L);

        userDto1 = new UserDto(1L, "userDtoname1", "userDto1Email@email");
        userDto2 = new UserDto(2L, "userDtoname2", "userDto2Email@email");

        user1 = UserMapper.toUser(userDto1);
        user2 = UserMapper.toUser(userDto2);

        itemDto1 = new ItemDto(1L, "itemName", "itemDescription", true, lastBooking,
                nextBooking, null, null);
        item1 = ItemMapper.toItem(itemDto1);
        userStorage.save(user1);
        userStorage.save(user2);
        storage.save(item1);
        userService.create(userDto1);
        userService.create(userDto2);
        itemDtoNew = itemService.create(user1.getId(), itemDto1);
        BookingInputDto bookingInputDto = new BookingInputDto(1L, item1.getId(), LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(7));
        Booking booking = BookingMapper.toBooking(bookingInputDto);
        bookingDto1 = BookingMapper.toDto(booking);
        bookingStorage.save(booking);
        bookingService.create(bookingInputDto, user2.getId());

        commentDto1 = new CommentDto(1L, "CommentText1", item1.getId(), user2.getName(), LocalDateTime.now());
        comment1 = CommentMapper.toComment(commentDto1);
    }

    @Test
    void create() {
        assertEquals(itemDtoNew.getId(), itemDto1.getId());
    }

    @Test
    void addCommentWithoutBookingByUser() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(user2.getId(), itemDtoNew.getId(), commentDto1));
        assertEquals("Пользователь с id 2не бронировал вещь с id 1", exception.getMessage());
    }

    @Test
    void addComment() {
        BookingInputDto bookingInputDto3 = new BookingInputDto(3L, item1.getId(), LocalDateTime.now().minusDays(20),
                LocalDateTime.now().minusDays(4));
        Booking booking3 = BookingMapper.toBooking(bookingInputDto3);
        bookingStorage.save(booking3);
        bookingService.create(bookingInputDto3, user2.getId());
        CommentDto commentDto = itemService.addComment(user2.getId(), item1.getId(), commentDto1);
        assertEquals(1L, commentDto.getId());
    }

    @Test
    void addCommentWithoutText() {
        commentDto1.setText("");
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(user2.getId(), itemDtoNew.getId(), commentDto1));
        assertEquals("нет текста комментария", exception.getMessage());
    }

    @Test
    void getAll() {
        BookingInputDto bookingInputDto3 = new BookingInputDto(3L, item1.getId(), LocalDateTime.now().minusDays(20),
                LocalDateTime.now().minusDays(4));
        Booking booking3 = BookingMapper.toBooking(bookingInputDto3);
        bookingStorage.save(booking3);
        bookingService.create(bookingInputDto3, user2.getId());
        CommentDto commentDto = itemService.addComment(user2.getId(), item1.getId(), commentDto1);
        assertEquals(1, itemService.getAll(user1.getId(), 1, 10).size());
    }

    @Test
    void getAllWithWrong() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.getAll(user1.getId(), -1, -2));
        assertEquals(exception.getMessage(), "не верно указан количество позиций на странице");
    }

    @Test
    void getById() {
        BookingInputDto bookingInputDto3 = new BookingInputDto(3L, item1.getId(), LocalDateTime.now().minusDays(20),
                LocalDateTime.now().minusDays(4));
        Booking booking3 = BookingMapper.toBooking(bookingInputDto3);
        bookingStorage.save(booking3);
        bookingService.create(bookingInputDto3, user2.getId());
        CommentDto commentDto = itemService.addComment(user2.getId(), item1.getId(), commentDto1);
        assertEquals(itemService.getById(user1.getId(), itemDto1.getId()).getId(), 1L);
    }

    @Test
    void getByIdWithWrongItemId() {
        final NotFoundObjectException exception = assertThrows(NotFoundObjectException.class,
                () -> itemService.getById(user1.getId(), 99L).getId());

        assertEquals(exception.getMessage(), "Товар с id " + 99L + " не зарегестрирован");
    }

    @Test
    void update() {
        ItemDto itemDtoNew = new ItemDto(1L, "updateItemDto", null, true, null, null, null, null);
        ItemDto itemDtoupdate = itemService.update(item1.getId(), itemDtoNew, user1.getId());
        assertEquals(itemDtoupdate.getName(), "updateItemDto");
    }

    @Test
    void updateWithEmtyItem() {
        ItemDto itemDtoNew = new ItemDto(1L, null, "updateDescription", null, null, null, null, null);
        ItemDto itemDtoupdate = itemService.update(item1.getId(), itemDtoNew, user1.getId());
        assertEquals(itemDtoupdate.getDescription(), "updateDescription");
    }

    @Test
    void search() {
        List<ItemDto> itemDescriptionAfterSearch = itemService.search("itemdescrip", 1, 10);
        assertEquals(itemDescriptionAfterSearch.size(), 1);
    }

    @Test
    void searchWithEmptyText() {
        List<ItemDto> itemDescriptionAfterSearch = itemService.search("", 1, 10);
        assertEquals(itemDescriptionAfterSearch.size(), 0);
    }

    @Test
    void searchWithWrongPage() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.search("itemdescrip", -1, -10));
        assertEquals("не верно указан количество позиций на странице", exception.getMessage());
    }
}
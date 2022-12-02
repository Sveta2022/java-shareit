package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest {

    private BookingController bookingController;
    private UserController userController;
    private ItemController itemController;
    private BookingInputDto bookingInputDto1;
    private BookingDto bookingDtoNew;
    private ItemDto itemDto1;
    private UserDto userDto1;
    private UserDto userDto2;

    @Autowired
    public BookingControllerTest(BookingController bookingController, UserController userController, ItemController itemController) {
        this.bookingController = bookingController;
        this.userController = userController;
        this.itemController = itemController;
    }

    @BeforeEach
    void start() {
        itemDto1 = new ItemDto(1L, "itemDtoName1", "itemDtoDescription1",
                true, null, null, null, null);
        userDto1 = new UserDto(1L, "user1", "user1@user1.ru");
        userDto2 = new UserDto(2L, "user2", "user1@user2.ru");

        userController.create(userDto1);
        userController.create(userDto2);
        itemController.create(userDto1.getId(), itemDto1);

        bookingInputDto1 = new BookingInputDto(1L, itemDto1.getId(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5));
        bookingDtoNew = bookingController.create(bookingInputDto1, userDto2.getId());
    }

    @Test
    void create() {
        assertEquals(bookingInputDto1.getId(), bookingDtoNew.getId());
    }

    @Test
    void getAllforBooker() {
        assertEquals(1, bookingController.getAllforBooker(userDto2.getId(),
                "FUTURE", 1, 10).size());
    }

//    @Test
//    void getAllforBookerUnknownState() {
//        final NoligalArgumentException exception = assertThrows(NoligalArgumentException.class,
//                () -> bookingController.getAllforBooker(userDto2.getId(), "Unknown", 1, 10).size());
//
//        assertEquals("Unknown state: Unknown", exception.getMessage());
//    }

    @Test
    void getAllforOwner() {
        assertEquals(1, bookingController.getAllforOwner(userDto1.getId(), "FUTURE", 1, 10).size());
    }

    @Test
    void getById() {
        assertEquals(1L, bookingController.getById(bookingInputDto1.getId(), userDto2.getId()).getId());
    }

    @Test
    void update() {
        BookingDto bookingDtoUpdate = bookingController.update(bookingInputDto1.getId(), userDto1.getId(), true);
        assertNotNull(bookingDtoUpdate);
    }
}
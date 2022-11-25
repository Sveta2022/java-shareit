package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {

    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private BookingController bookingController;


    private UserDto userDto1;
    private UserDto userDto2;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemDto itemDtoTest;

    private CommentDto commentDto1;

    private BookingInputDto bookingInputDto;

    @BeforeEach
    void start() {
        userDto1 = new UserDto(1L, "user1", "user1@user1.ru");
        userDto2 = new UserDto(2L, "user2", "user1@user2.ru");
        itemDto1 = new ItemDto(1L, "nameItemDto", "descriptionItem",
                true, null, null, null, null);
        bookingInputDto = new BookingInputDto(1L, itemDto1.getId(), LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(3));
        commentDto1 = new CommentDto(1L, "commentText", itemDto1.getId(), userDto2.getName(),
                LocalDateTime.now());
        userController.create(userDto1);
        userController.create(userDto2);
        itemDtoTest = itemController.create(userDto1.getId(), itemDto1);
        bookingController.create(bookingInputDto, userDto2.getId());
    }


    @Test
    void create() {
        assertNotNull(itemDtoTest);
    }

    @Test
    void update() {
        ItemDto itemDtoUpdate = new ItemDto(2L, "itemUpdate", null, null,
                null, null, null, null);
        ItemDto update = itemController.update(itemDtoUpdate, itemDto1.getId(), userDto1.getId());
        assertEquals("itemUpdate", update.getName());
    }

    @Test
    void getAll() {
        List<ItemDto> items = itemController.getAll(userDto1.getId(), 1, 10);
        assertEquals(1, items.size());
    }

    @Test
    void getById() {
        ItemDto itemDto = itemController.getById(userDto1.getId(), itemDto1.getId());
        assertEquals(1, itemDto.getId());
    }

    @Test
    void search() {
        List<ItemDto> items = itemController.search("descriptionIt", 1, 10);
        assertEquals(1, items.size());
    }

    @Test
    void addComment() {
        CommentDto commentDto = itemController.addComment(userDto2.getId(), itemDto1.getId(), commentDto1);
        assertNotNull(commentDto);

    }

}
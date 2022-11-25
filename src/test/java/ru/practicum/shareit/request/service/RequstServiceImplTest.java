package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.validation.exception.NotFoundObjectException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequstServiceImplTest {

    @Mock
    UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private RequestStorage requestStorage;

    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    UserServiceImpl userService;

    @Autowired
    RequstServiceImpl requstService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private Item item1;
    private Item item2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequest itemRequest1;
    private ItemRequestDto requestDtoNew;

    @Autowired
    public RequstServiceImplTest(UserStorage userStorage, ItemStorage itemStorage, RequestStorage requestStorage, ItemServiceImpl itemService, UserServiceImpl userService, RequstServiceImpl requstService) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.requestStorage = requestStorage;
        this.itemService = itemService;
        this.userService = userService;
        this.requstService = requstService;
    }

    @BeforeEach
    void start() {
        requstService.storage = requestStorage;
        requstService.userStorage = userStorage;
        requstService.itemStorage = itemStorage;

        LocalDateTime startLastBooking = LocalDateTime.now().minusDays(7);
        LocalDateTime endLastBooking = LocalDateTime.now().minusDays(3);
        LocalDateTime startNextBooking = LocalDateTime.now().plusDays(3);
        LocalDateTime endNextBooking = LocalDateTime.now().plusDays(7);
        ItemDto.BookingDtoItem lastBooking = new ItemDto.BookingDtoItem(2L, startLastBooking, endLastBooking, 1L);
        ItemDto.BookingDtoItem nextBooking = new ItemDto.BookingDtoItem(3L, startNextBooking, endNextBooking, 2L);

        userDto1 = new UserDto(1L, "userDtoname1", "userDto1Email@email");
        userDto2 = new UserDto(2L, "userDtoname2", "userDto2Email@email");

        user1 = UserMapper.toUser(userDto1);
        user2 = UserMapper.toUser(userDto2);

        itemDto1 = new ItemDto(1L, "itemName", "itemDescription", true, lastBooking,
                nextBooking, null, null);
        item1 = ItemMapper.toItem(itemDto1);
        ItemRequestDto.ItemAnswerDto itemAnswerDto =
                new ItemRequestDto.ItemAnswerDto(3L, "itemAnswer1", "itemAnswerDescription1",
                        user1.getId(), true, user2.getId());
        itemRequestDto1 = new ItemRequestDto(1L, "requestDescription1", null, LocalDateTime.now(), List.of(itemAnswerDto));
        itemRequest1 = RequestMapper.toRequest(itemRequestDto1);

        userStorage.save(user1);
        userStorage.save(user2);
        userService.create(userDto1);
        userService.create(userDto2);
        requestStorage.save(itemRequest1);
        requestDtoNew = requstService.create(user2.getId(), itemRequestDto1);
        item1.setItemRequest(itemRequest1);
        itemStorage.save(item1);
        userService.create(userDto1);
        userService.create(userDto2);
        itemDto1.setRequestId(1L);
        itemService.create(user1.getId(), itemDto1);
        requestStorage.save(itemRequest1);
        requestDtoNew = requstService.create(user2.getId(), itemRequestDto1);

    }

    @Test
    void create() {
        assertEquals(requestDtoNew.getId(), itemRequest1.getId());
    }

    @Test
    void createWrongUser() {

        final NotFoundObjectException exception = assertThrows(NotFoundObjectException.class,
                () -> requstService.create(99L, itemRequestDto1));

        assertEquals("Пользователь с id " + 99L + " не зарегестрирован", exception.getMessage());
    }

    @Test
    void getAll() {
        List<ItemRequestDto> requestsByCreator = requstService.getAll(user2.getId());
        assertEquals(requestsByCreator.size(), 1);
    }

    @Test
    void getById() {
        ItemRequestDto itemRequestDtoNew = requstService.getById(user2.getId(), itemRequest1.getId());
        assertEquals(itemRequestDtoNew.getId(), itemRequest1.getId());

    }

    @Test
    void getAllByPage() {
        List<ItemRequestDto> requestsByCreator = requstService.getAllByPage(user1.getId(), 1, 10);
        assertEquals(requestsByCreator.size(), 1);
    }

    @Test
    void getAllByPageWithWrong() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> requstService.getAllByPage(user1.getId(), -1, -10));
        assertEquals("не верно указан количество позиций на странице", exception.getMessage());
    }
}
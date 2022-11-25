package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.NotFoundObjectException;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Mock
    UserStorage userStorage;
    @Autowired
    UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @Autowired
    public UserServiceTest(UserServiceImpl userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;

    }

    @BeforeEach
    void start() {
        userService.storage = userStorage;
        userDto1 = new UserDto(1L, "userDtoname1", "userDto1Email@email");
        userDto2 = new UserDto(2L, "userDtoname2", "userDto2Email@email");

        user1 = UserMapper.toUser(userDto1);
        user2 = UserMapper.toUser(userDto2);
        userStorage.save(user1);
        userStorage.save(user2);
    }

    @Test
    void create() {
        when(userStorage.save(user1))
                .thenReturn(user1);

        userService.create(userDto1);
        UserDto userDtoNew = userService.getById(userDto1.getId());

        assertEquals(userDtoNew.getId(), userDto1.getId());

        Mockito.verify(userStorage, Mockito.times(1))
                .save(user1);

    }

    @Test
    void update() {
        userService.create(userDto1);
        userService.update(1L, userDto2);
        UserDto userDtoNew = userService.getById(userDto1.getId());
        assertEquals(userDtoNew.getName(), userDto2.getName());
    }

    @Test
    void updatewithUserNullParametrs() {
        userStorage.save(user1);
        userService.create(userDto1);
        UserDto userDtoNew = new UserDto(2L, null, null);
        UserDto update = userService.update(userDto1.getId(), userDtoNew);

    }

    @Test
    void getById() {
        userStorage.findById(1L);
        when(userStorage.findById(1L))
                .thenReturn(Optional.of(user1));

        userService.create(userDto1);
        UserDto userDtoNew = userService.getById(userDto1.getId());

        assertEquals(userDtoNew.getId(), userDto1.getId());

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(1L);
    }

    @Test
    void getByIdUserNotExist() {
        userStorage.findById(99L);
        when(userStorage.findById(99L))
                .thenReturn(Optional.ofNullable(user1));

        assertThrows(NotFoundObjectException.class,
                () -> userService.getById(99L));

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(99L);
    }

    @Test
    void delete() {

        userService.create(userDto1);
        userService.delete(1L);

        assertEquals(userService.getAll().size(),0);

    }

    @Test
    void getAll() {
        userService.create(userDto1);
        userService.create(userDto2);
        List<UserDto> userDtos = userService.getAll();

        userStorage.findAll();

        when(userStorage.findAll())
                .thenReturn(List.of(user1, user2));
        assertEquals(userDtos.size(), 2);

        Mockito.verify(userStorage, Mockito.times(1))
                .findAll();

    }
}
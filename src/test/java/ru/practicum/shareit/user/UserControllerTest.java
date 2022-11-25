package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.user.dto.UserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;



@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private UserController userController;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void start() {
        userDto1 = new UserDto(1L, "user1", "user1@user1.ru");
        userDto2 = new UserDto(2L, "user2", "user1@user2.ru");
    }

    @Test
    void create() {
        UserDto userDtoNew1 = userController.create(userDto1);
        assertEquals(userDto1.getName(), userDtoNew1.getName());
    }

    @Test
    void getAll() {
        userController.create(userDto1);
        userController.create(userDto2);
        List<UserDto> userDtos = userController.getAll();
        assertEquals(userDtos.size(), 2);
    }

    @Test
    void  getById() {
        UserDto userDtoNew1 = userController.create(userDto1);
        UserDto userDtoNew2 = userController.create(userDto2);
        UserDto userDtoNew = userController.getById(1L);
        assertEquals(userDtoNew.getId(), userDtoNew1.getId());

    }

    @Test
    void update() {
        UserDto userDtoNew1 = userController.create(userDto1);
        userDtoNew1 = userController.update(userDto2, 1L);
        assertEquals(userDtoNew1.getName(), userDto2.getName());
    }

    @Test
    void delete() {
        UserDto userDtoNew1 = userController.create(userDto1);
        UserDto userDtoNew2 = userController.create(userDto2);
        userController.delete(1L);
        assertEquals(userController.getAll().size(), 1);
    }

}
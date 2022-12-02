package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;


    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class})
                                         @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя " + userDto.getName() + " id " + userDto.getId() + " создан");
        return userClient.create(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получить всех пользователей создан");
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя по id " + id + " создан");
        return userClient.getById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto,
                                         @PathVariable Long id) {
        log.info("Запрос на обновление пользователя " + userDto.getName() + " id " + userDto.getId() + " создан");
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Запрос на обновление пользователя с id " + id + " создан");
        return userClient.delete(id);
    }
}

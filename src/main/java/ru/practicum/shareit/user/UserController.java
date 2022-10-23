package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public UserDto create(@Validated({Create.class})
                          @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя " + userDto.getName() + " id " + userDto.getId() + " создан");
        return service.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос на добавление пользователя создан");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Запрос на получение пользователя по id " + id + " создан");
        return service.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable long id) {
        log.info("Запрос на обновление пользователя " + userDto.getName() + " id " + userDto.getId() + " создан");
        return service.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Запрос на обновление пользователя с id " + id + " создан");
        service.delete(id);
    }
}

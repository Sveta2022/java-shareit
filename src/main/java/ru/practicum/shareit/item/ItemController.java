package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    ItemService service;

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи для пользователя " + userId + " создан");
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable long id,
                          @Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на обновление вещи с id " + id + " создан");
        return service.update(id, itemDto, userId);
    }

    @DeleteMapping
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление вещи с id " + id + " создан");
        service.delete(id);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех вещей пользователя с id " + userId + " создан");
        return service.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id) {
        log.info("Запрос на получение пользователя по id " + id + " создан");
        return service.getById(id);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        log.info("Запрос на поиск вещей по тексту " + text + " создан");
        return service.search(text);
    }
}

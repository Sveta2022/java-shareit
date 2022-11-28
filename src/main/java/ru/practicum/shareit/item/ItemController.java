package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи для пользователя " + userId + " создан");
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable long id,
                          @Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление вещи с id " + id + " создан");
        return service.update(id, itemDto, userId);
    }


    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение списка всех вещей пользователя с id " + userId + " создан");
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        log.info("Запрос на получение вещи по id " + id + " создан");
        return service.getById(userId, id);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDto> search(@RequestParam(name = "text") String text,
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Запрос на поиск вещей по тексту " + text + " создан");
        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Long authorId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария для пользователя " + authorId + " создан");
        return service.addComment(authorId, itemId, commentDto);
    }

}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи для пользователя " + userId + " создан");
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto itemDto,
                                         @PathVariable long id,
                                         @Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление вещи с id " + id + " создан");
        return itemClient.update(id, itemDto, userId);
    }


    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение списка всех вещей пользователя с id " + userId + " создан");
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id) {
        log.info("Запрос на получение вещи по id " + id + " создан");
        return itemClient.getById(userId, id);
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Object> search(@RequestParam(name = "text") String text,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Запрос на поиск вещей по тексту " + text + " создан");
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Long authorId,
                                             @PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария для пользователя " + authorId + " создан");
        return itemClient.addComment(authorId, itemId, commentDto);
    }


}

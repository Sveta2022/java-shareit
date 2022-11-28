package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.validation.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
   private RequestService service;

    @Autowired
    public ItemRequestController(RequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestDto create(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на создание запроса для пользователя " + userId + " создан");
        return service.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение запроса для пользователя c ответом " + userId + " создан");
        return service.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long requestId) {
        log.info("Запрос на получение запроса по id " + requestId + " для пользователя " + userId + " создан");
        return service.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllByPage(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение постраничного запроса для пользователя " + userId + " создан");
        return service.getAllByPage(userId, from, size);
    }

}

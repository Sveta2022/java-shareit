package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import java.util.stream.Collectors;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository
public class ItemStorageImplInMemory implements ItemStorage {
    private Map<Long, Item> items = new HashMap<>();
    private Long idGenerator = 0L;

    @Override
    public Item save(User user, Item item) {
        idGenerator++;
        item.setOwner(user);
        item.setId(idGenerator);
        items.put(idGenerator, item);
        log.info("Запрос на создание вещи с id " + item.getId() + " обработан");
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        log.info("Запрос на получить список вещей обработан");
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long id) {
        log.info("Запрос на поиск вещи с id " + id + " обработан");
        Item item = items.get(id);
        if (item == null) {
            throw new ValidationException("Товар с id " + id + " не зарегестрирован");
        }
        return item;
    }

    @Override
    public Item update(Item itemNew) {
        items.put(itemNew.getId(), itemNew);
        log.info("Запрос на обновление вещь с id " + itemNew.getId() + " обработан");
        return itemNew;
    }

    @Override
    public void delete(Long id) {
        log.info("Запрос на удаление вещи с id " + id + " обработан");
        items.remove(id);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> fitItemDto = new ArrayList<>();
        if (text.isBlank()) {
            return fitItemDto;
        }
        if (items != null) {
            for (Item item : items.values()) {
                if ((item.getAvailable()) && (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))) {
                    fitItemDto.add(item);
                }
            }
        }
        log.info("Запрос на поиска вещей с текстом " + text + " обработан");
        return fitItemDto;
    }
}

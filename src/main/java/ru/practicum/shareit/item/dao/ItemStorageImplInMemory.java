package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository
public class ItemStorageImplInMemory implements ItemStorage {
    private List<Item> items = new ArrayList<>();
    private Long idGenerator = 0L;

    @Override
    public Item create(User user, Item item) {
        if (items != null) {
            for (Item itemNew : items) {
                if (itemNew.getId() > idGenerator) {
                    idGenerator = itemNew.getId();
                }
            }
        }
        idGenerator++;
        item.setId(idGenerator);
        item.setOwner(user);
        items.add(item);
        log.info("Запрос на создание вещи с id " + item.getId() + " обработан");
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        log.info("Список вещей обработан");
        return items.stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long id) {
        log.info("Запрос на поиск вещи с id " + id + " обработан");
        return items.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .orElseThrow(() -> new ValidationException("Товар с id " + id + " не зарегестрирован"));
    }

    @Override
    public Item update(Item itemold, Item itemNew, User user) {
        itemNew.setId(itemold.getId());
        items.remove(itemold);
        items.add(itemNew);
        log.info("Запрос на обновление вещь с id " + itemNew.getId() + " обработан");
        return itemNew;
    }

    @Override
    public void delete(Item item) {
        log.info("Запрос на удаление вещи с id " + item.getId() + " обработан");
        items.remove(item);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> fitItemDto = new ArrayList<>();
        if (text.isBlank()) {
            return fitItemDto;
        }
        if (items != null) {
            for (Item item : items) {
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

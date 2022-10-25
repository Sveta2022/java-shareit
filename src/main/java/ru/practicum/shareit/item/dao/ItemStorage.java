package ru.practicum.shareit.item.dao;


import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item save(User userId, Item item);

    List<Item> getAll(Long userId);

    Item getById(Long id);

    Item update(Item itemNew);

    void delete(Long id);

    List<Item> search(String text);
}

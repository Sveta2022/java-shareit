package ru.practicum.shareit.item.dao;


import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item create(User user, Item item);

    List<Item> getAll(Long userId);

    Item getById(Long id);

    Item update(Item itemold, Item itemNew, User user);

    void delete(Item item);

    List<Item> search(String text);
}

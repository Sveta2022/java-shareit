package ru.practicum.shareit.user.dao;


import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserStorage {
    User save(User user);

    User update(User userNew);

    User getById(long id);

    void delete(Long id);

    List<User> getAll();
}

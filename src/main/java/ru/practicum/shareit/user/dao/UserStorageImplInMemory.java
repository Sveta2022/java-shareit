package ru.practicum.shareit.user.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundObjectException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
@Primary
@RequiredArgsConstructor
@Getter
public class UserStorageImplInMemory implements UserStorage {

    private final List<User> users = new ArrayList<>();
    private Long idGenerator = 0L;

    @Override
    public User save(User user) {
        if (users != null) {
            for (User userNew : users) {
                if (userNew.getId() > idGenerator) {
                    idGenerator = userNew.getId();
                }
            }
        }
        idGenerator++;
        user.setId(idGenerator);
        users.add(user);
        log.info("Запрос на добавление пользователя c id " + user.getId() + " обработан");
        return user;
    }

    @Override
    public User update(User userOld, User userNew) {
        userNew.setId(userOld.getId());
        users.remove(userOld);
        users.add(userNew);
        log.info("Запрос на обновление пользователя c id " + userNew.getId() + " обработан");
        return userNew;
    }

    @Override
    public User getById(long id) {
        log.info("Запрос на поиск пользователя c id " + id + " обработан");
        return users.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .orElseThrow(() -> new NotFoundObjectException("Пользователя с id " + id + " не зарегестрирован"));
    }

    @Override
    public void delete(User user) {
        log.info("Запрос на удаление пользователя c id " + user.getId() + " обработан");
        users.remove(user);
    }

    @Override
    public List<User> getAll() {
        log.info("Запрос на получение списка пользователей обработан");
        return users;
    }
}

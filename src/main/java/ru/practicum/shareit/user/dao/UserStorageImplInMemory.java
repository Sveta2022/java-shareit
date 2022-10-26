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
    private final Map<Long, User> users = new HashMap<>();
    private Long idGenerator = 0L;

    @Override
    public User save(User user) {
        idGenerator++;
        user.setId(idGenerator);
        users.put(idGenerator, user);
        log.info("Запрос на добавление пользователя c id " + user.getId() + " обработан");
        return user;
    }

    @Override
    public User update(User userNew) {

        users.put(userNew.getId(), userNew);
        log.info("Запрос на обновление пользователя c id " + userNew.getId() + " обработан");
        return userNew;
    }

    @Override
    public User getById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundObjectException("Пользователя с id " + id + " не зарегестрирован");
        }
        log.info("Запрос на поиск пользователя c id " + id + " обработан");
        return user;
    }

    @Override
    public void delete(Long id) {
        log.info("Запрос на удаление пользователя c id " + id + " обработан");
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        log.info("Запрос на получение списка пользователей обработан");
        return new ArrayList<>(users.values());
    }
}

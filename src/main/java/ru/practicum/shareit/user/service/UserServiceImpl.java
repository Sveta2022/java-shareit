package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.exception.NotFoundObjectException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(storage.save(user));
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User userOld = UserMapper.toUser(getById(id));
        User userNew = UserMapper.toUser(userDto);
        if (userNew.getName() == null) {
            userNew.setName(userOld.getName());
        }
        if (userNew.getEmail() == null) {
            userNew.setEmail(userOld.getEmail());
        }
        userNew.setId(id);
        return UserMapper.toDto(storage.save(userNew));
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = storage.findById(id);
        if (user.isPresent()) {
            return UserMapper.toDto(user.get());
        }
        throw new NotFoundObjectException("Пользователя с id " + id + " не зарегестрирован");
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User user = UserMapper.toUser(getById(id));
        storage.delete(user);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
        }



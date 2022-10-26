package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AllOtherException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        dublicateMailCheck(user.getEmail());
        return UserMapper.toDto(storage.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User userOld = UserMapper.toUser(getById(id));
        User userNew = UserMapper.toUser(userDto);
        dublicateMailCheck(userNew.getEmail());
        if (userNew.getName() == null) {
            userNew.setName(userOld.getName());
        }
        if (userNew.getEmail() == null) {
            userNew.setEmail(userOld.getEmail());
        }
        userNew.setId(id);
        return UserMapper.toDto(storage.update(userNew));
    }

    @Override
    public UserDto getById(long id) {
        return UserMapper.toDto(storage.getById(id));
    }

    @Override
    public void delete(long id) {
        User user = UserMapper.toUser(getById(id));
        storage.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.getAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    private void dublicateMailCheck(String email) {
        if (storage.getAll() != null) {
            if (storage.getAll().stream()
                    .anyMatch(u -> u.getEmail().equals(email))) {
                throw new AllOtherException("Пользователь с email " + email + " уже существует");
            }
        }
    }
}

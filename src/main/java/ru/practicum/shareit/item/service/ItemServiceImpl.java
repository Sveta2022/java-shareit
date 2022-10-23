package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundObjectException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemStorage storage;
    @Autowired
    UserService userService;


    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = UserMapper.toUser(userService.getById(userId));
        if (userId == 0) {
            throw new NotFoundObjectException("Не найден id " + userId + " пользователя");
        }
        return ItemMapper.toDto(storage.create(user, item));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return storage.getAll(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toDto(storage.getById(id));
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDtoNew, Long userId) {
        Item itemOld = storage.getById(id);
        User user = UserMapper.toUser(userService.getById(userId));
        Item itemNew = ItemMapper.toItem(itemDtoNew);
        if (itemNew.getName() == null) {
            itemNew.setName(itemOld.getName());
        }
        if (itemNew.getDescription() == null) {
            itemNew.setDescription(itemOld.getDescription());
        }
        if (itemNew.getOwner().getId() == 0) {
            itemNew.setOwner(itemOld.getOwner());
        }
        if (itemNew.getAvailable() == null) {
            itemNew.setAvailable(itemOld.getAvailable());
        }
        if (itemNew.getRequest() == null) {
            itemNew.setRequest(itemOld.getRequest());
        }
        if (!itemNew.getOwner().equals(user)) {
            throw new NotFoundObjectException("Не верно указан id владельца вещи");
        }
        return ItemMapper.toDto(storage.update(itemOld, itemNew, user));
    }

    @Override
    public void delete(Long id) {
        Item item = ItemMapper.toItem(getById(id));
        storage.delete(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        return storage.search(text.toLowerCase()).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}

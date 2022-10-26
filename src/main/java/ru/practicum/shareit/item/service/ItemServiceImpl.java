package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundObjectException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemStorage storage;
    private UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userStorage.getById(userId);
        return ItemMapper.toDto(storage.save(user, item));
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
        User user = userStorage.getById(userId);
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
        itemNew.setId(id);
        return ItemMapper.toDto(storage.update(itemNew));
    }

    @Override
    public void delete(Long id) {
        Item item = ItemMapper.toItem(getById(id));
        storage.delete(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        return storage.search(text.toLowerCase()).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}

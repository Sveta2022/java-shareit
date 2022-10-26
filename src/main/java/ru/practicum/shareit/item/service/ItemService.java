package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    List<ItemDto> getAll(Long userId);

    ItemDto getById(Long id);

    ItemDto update(Long id, ItemDto itemDto, Long userId);

    void delete(Long id);

    List<ItemDto> search(String text);
}

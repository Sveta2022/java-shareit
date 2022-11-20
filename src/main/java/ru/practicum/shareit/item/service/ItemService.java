package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    List<ItemDto> getAll(Long userId, int from, int size);

    ItemDto getById(Long userId, Long id);

    ItemDto update(Long id, ItemDto itemDto, Long userId);

    void delete(Long id, Long userId);

    CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto);

    List<ItemDto> search(String text,  int from, int size);
}

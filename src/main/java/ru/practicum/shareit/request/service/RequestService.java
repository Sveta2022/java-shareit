package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAll(Long userId);

    ItemRequestDto getById(Long userId, Long id);

    List<ItemRequestDto> getAllByPage(Long userId, Integer from, Integer size);
}

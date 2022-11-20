package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.exception.NotFoundObjectException;
import ru.practicum.shareit.validation.exception.ValidationException;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequstServiceImpl implements RequestService {
    RequestStorage storage;
    UserStorage userStorage;
    ItemStorage itemStorage;

    @Autowired
    public RequstServiceImpl(RequestStorage storage, UserStorage userStorage, ItemStorage itemStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }


    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User userCreator = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Пользователь с id " + userId + " не зарегестрирован"));
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto);
        itemRequest.setRequestor(userCreator);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest request = storage.save(itemRequest);
        itemRequestDto = RequestMapper.toDto(request);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        User userCreator = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Пользователь с id " + userId + " не зарегестрирован"));
        List<ItemRequest> requests = storage.findByUserId(userId);
        List<ItemRequestDto> requestDtos = requests.stream()
                .map(RequestMapper::toDto).collect(Collectors.toList());
        List<ItemRequestDto> requestDtosWithAnswer = new ArrayList<>();
        for (ItemRequestDto requestDto : requestDtos) {
            Long idRequestDto = requestDto.getId();
            if (idRequestDto != 0) {
                List<Item> items = itemStorage.findByIdRequest(idRequestDto);
                List<ItemRequestDto.ItemAnswerDto> newItemList = new ArrayList<>();
                for (Item item : items) {
                    newItemList.add(ItemRequestDto.ItemAnswerDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .idOwner(item.getOwner().getId())
                            .requestId(item.getItemRequest().getId())
                            .description(item.getDescription())
                            .available(item.getAvailable())
                            .build());
                }
                requestDto.setItems(newItemList);
                requestDtosWithAnswer.add(requestDto);
            }
        }
        return requestDtosWithAnswer;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long id) {
        User userCreator = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Пользователь с id " + userId + " не зарегестрирован"));

        ItemRequest request = storage.findById(id)
                .orElseThrow(() -> new NotFoundObjectException("Запрос с id " + id + " не найден"));
        ItemRequestDto requestDto = RequestMapper.toDto(request);
        List<Item> items = itemStorage.findByIdRequest(requestDto.getId());
        List<ItemRequestDto.ItemAnswerDto> newItemList = new ArrayList<>();
        for (Item item : items) {
            newItemList.add(ItemRequestDto.ItemAnswerDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .idOwner(item.getOwner().getId())
                    .requestId(item.getItemRequest().getId())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .build());
        }
        requestDto.setItems(newItemList);
        return requestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByPage(Long userId, Integer from, Integer size) {
        User userCreator = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Пользователь с id " + userId + " не зарегестрирован"));
        if (from < 0 || size <= 0) {
            throw new ValidationException("не верно указан количество позиций на странице");
        }
        int page = from / size;
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        final PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);
        List<ItemRequest> itemRequests = storage.findAllByUserID(userId, pageRequest)
                .stream().collect(Collectors.toList());

        List<ItemRequestDto> requestDtos = itemRequests.stream()
                .map(RequestMapper::toDto).collect(Collectors.toList());
        List<ItemRequestDto> requestDtosWithAnswer = new ArrayList<>();
        for (ItemRequestDto requestDto : requestDtos) {
            Long idRequestDto = requestDto.getId();
            if (idRequestDto != 0) {
                List<Item> items = itemStorage.findByIdRequest(idRequestDto);
                List<ItemRequestDto.ItemAnswerDto> newItemList = new ArrayList<>();
                for (Item item : items) {
                    newItemList.add(ItemRequestDto.ItemAnswerDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .idOwner(item.getOwner().getId())
                            .requestId(item.getItemRequest().getId())
                            .description(item.getDescription())
                            .available(item.getAvailable())
                            .build());
                }
                requestDto.setItems(newItemList);
                requestDtosWithAnswer.add(requestDto);
            }
        }
        return requestDtosWithAnswer;
    }
}

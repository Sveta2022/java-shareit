package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.request.exception.NotFoundObjectException;
import ru.practicum.shareit.request.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private ItemStorage storage;
    private UserStorage userStorage;
    private CommentRepository commentStorage;
    private BookingStorage bookingStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage storage, UserStorage userStorage,
                           CommentRepository commentStorage, BookingStorage bookingStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.commentStorage = commentStorage;
        this.bookingStorage = bookingStorage;
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userStorage.findById(userId).get();
        item.setOwner(user);
        return ItemMapper.toDto(storage.save(item));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("нет текста комментария");
        }
        User author = userStorage.findById(authorId)
                .orElseThrow(() -> new ValidationException("нет такого пользователя"));
        Item item = storage.findById(itemId)
                .orElseThrow(() -> new ValidationException("нет такого пользователя"));
        bookingStorage.findAllByBookerIdAndEndBeforeOrderByIdDesc(authorId, LocalDateTime.now())
                .stream().findFirst()
                .orElseThrow(() -> new ValidationException("Пользователь с id " +
                        authorId + "не бронировал вещь с id " + itemId));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        CommentDto commentDtoNew = CommentMapper.toDto(commentStorage.save(comment));
        return commentDtoNew;
    }


    @Override
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> itemDtos = storage.findAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        List<ItemDto> itemNewDtos = new ArrayList<>();
        for (ItemDto itemDto : itemDtos) {
            Long id = itemDto.getId();
            Booking lastBookings = bookingStorage.findFirstByItemIdAndEndBeforeOrderByEndDesc(id, LocalDateTime.now());
            Booking nextBookings = bookingStorage.findOneByItemIdAndStartAfterOrderByStartAsc(id, LocalDateTime.now());
            List<Comment> comments = commentStorage.findAllByItemId(id);

            List<CommentDto> commentsDto = new ArrayList<>();
            for (Comment comment : comments) {
                CommentDto commentDto = CommentMapper.toDto(comment);
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentDto.setIdItem(comment.getItem().getId());
                commentsDto.add(commentDto);
            }
            Item item = storage.findById(id).orElseThrow();
            User owner = item.getOwner();
            System.out.println(owner.getId());
            if (owner.getId() == userId) {
                if (lastBookings != null) {
                    itemDto.setLastBooking(new ItemDto.BookingDtoItem(lastBookings.getId(),
                            lastBookings.getStart(), lastBookings.getEnd(),
                            lastBookings.getBooker().getId()));
                } else {
                    itemDto.setLastBooking(null);
                }
                if (nextBookings != null) {
                    System.out.println("Третий");
                    itemDto.setNextBooking(new ItemDto.BookingDtoItem(nextBookings.getId(),
                            nextBookings.getStart(), nextBookings.getEnd(),
                            nextBookings.getBooker().getId()));
                } else {
                    itemDto.setNextBooking(null);
                }
            } else {
                System.out.println("Будет установлено нулевое значение");
                itemDto.setLastBooking(null);
                itemDto.setNextBooking(null);
            }
            if (commentsDto == null) {
                itemDto.setComments(null);
            } else {
                itemDto.setComments(commentsDto);
            }
            itemNewDtos.add(itemDto);
        }
        return itemNewDtos;
    }

    @Override
    public ItemDto getById(Long userId, Long id) {
        Item item = storage.findById(id)
                .orElseThrow(() -> new NotFoundObjectException("Товар с id " + id + " не зарегестрирован"));
        ItemDto itemDto = ItemMapper.toDto(item);
        Booking lastBookings = bookingStorage.findFirstByItemIdAndEndBeforeOrderByEndDesc(id, LocalDateTime.now());
        Booking nextBookings = bookingStorage.findOneByItemIdAndStartAfterOrderByStartAsc(id, LocalDateTime.now());
        List<Comment> comments = commentStorage.findAllByItemId(id);
        Long ownerId = item.getOwner().getId();
        if (ownerId == userId) {
            if (lastBookings != null) {
                itemDto.setLastBooking(new ItemDto.BookingDtoItem(lastBookings.getId(),
                        lastBookings.getStart(), lastBookings.getEnd(),
                        lastBookings.getBooker().getId()));
            }
            if (nextBookings != null) {

                itemDto.setNextBooking(new ItemDto.BookingDtoItem(nextBookings.getId(),
                        nextBookings.getStart(), nextBookings.getEnd(),
                        nextBookings.getBooker().getId()));
            }
        } else {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = CommentMapper.toDto(comment);
            commentDto.setAuthorName(comment.getAuthor().getName());
            commentDto.setIdItem(comment.getItem().getId());
            commentsDto.add(commentDto);
        }
        if (commentsDto == null) {
            itemDto.setComments(null);
        } else {
            itemDto.setComments(commentsDto);
        }
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDtoNew, Long userId) {
        Item itemOld = storage.findById(id).get();
        User user = userStorage.findById(userId).get();
        Item itemNew = ItemMapper.toItem(itemDtoNew);
        if (itemNew.getName() == null) {
            itemNew.setName(itemOld.getName());
        }
        if (itemNew.getDescription() == null) {
            itemNew.setDescription(itemOld.getDescription());
        }
        if (itemNew.getOwner().getId() == null) {
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
        return ItemMapper.toDto(storage.save(itemNew));
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Item item = ItemMapper.toItem(getById(id, userId));
        storage.delete(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> fitItemDto = new ArrayList<>();
        if (text.isBlank()) {
            return fitItemDto;
        }
        return storage.findByDescriptionContainingIgnoreCase(text)
                .stream().filter(Item::getAvailable)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}

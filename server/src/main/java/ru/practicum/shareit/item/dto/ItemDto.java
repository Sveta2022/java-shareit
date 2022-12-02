package ru.practicum.shareit.item.dto;

import lombok.*;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")

public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoItem lastBooking = null;
    private BookingDtoItem nextBooking = null;
    private List<CommentDto> comments = new ArrayList<>();

    private Long requestId;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BookingDtoItem {
        Long id;
        LocalDateTime start;
        LocalDateTime end;
        Long bookerId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentDtoItem {
        Long id;
        String text;
        Item item;
        User author;
    }
}

package ru.practicum.shareit.item.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank(groups = Create.class)
    @Length(max = 30, groups = Create.class)
    private String name;

    @NotBlank(groups = Create.class)
    @Length(max = 100, groups = Create.class)
    private String description;

    @NotNull(groups = Create.class)
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

package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ItemRequestDto {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemAnswerDto> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemAnswerDto {
        private Long id;
        private String name;
        private String description;
        private Long idOwner;
        boolean available;
        private Long requestId;
    }
}

package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInputDto {

    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

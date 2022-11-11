package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInputDto {

    private Long id;

    private Long itemId;

    @FutureOrPresent
    LocalDateTime start;

    @FutureOrPresent
    LocalDateTime end;
}

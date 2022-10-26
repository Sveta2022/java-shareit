package ru.practicum.shareit.item.dto;

import lombok.*;
import org.apache.coyote.Request;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")

public class ItemDto {

    private long id;
    @NotBlank(groups = Create.class)
    @Length(max = 30, groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    @Length(max = 100, groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Request request;
}

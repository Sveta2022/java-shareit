package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class User {
    private long id;
    @NotBlank(groups = Create.class)
    private String name;
    //email должен быть уникальным
    @NotBlank(groups = Create.class)
    @Email(groups = Create.class)
    private String email;
}

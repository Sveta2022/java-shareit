package ru.practicum.shareit.item.model;

import lombok.*;
import org.apache.coyote.Request;
import ru.practicum.shareit.user.model.User;


import javax.persistence.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    @OneToOne(orphanRemoval = true, cascade = {CascadeType.ALL})
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @Transient
    private Request request;
}

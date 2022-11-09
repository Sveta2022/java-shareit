package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * add-bookings.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @OneToOne(orphanRemoval = true, cascade = {CascadeType.ALL})
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @OneToOne(orphanRemoval = true, cascade = {CascadeType.ALL})
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean isApproved;
}

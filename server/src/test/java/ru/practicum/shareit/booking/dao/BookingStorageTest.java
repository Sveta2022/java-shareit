package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingStorageTest {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private BookingStorage bookingStorage;

    private final TestEntityManager em;

    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;

    @Autowired
    public BookingStorageTest(UserStorage userStorage, ItemStorage itemStorage, BookingStorage bookingStorage, TestEntityManager em) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.bookingStorage = bookingStorage;
        this.em = em;
    }

    @BeforeEach
    void start() {
        user1 = new User(1L, "user1", "user1@email.ru");
        user2 = new User(2L, "user2", "user2@email.ru");
        item1 = new Item(1L, "itemName1", "descriptionItem1", user1, true, null);
        booking1 = new Booking(1L, LocalDateTime.of(2022, 12, 10, 10, 30),
                LocalDateTime.of(2022, 12, 18, 10, 30),
                item1, user2, Status.APPROVED, true);

        userStorage.save(user1);
        userStorage.save(user2);
        itemStorage.save(item1);
        bookingStorage.save(booking1);

    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("Select b from Booking b where b.booker.id = :idBooker order by b.start desc ", Booking.class);
        Booking booking = query.setParameter("idBooker", booking1.getBooker().getId()).getSingleResult();
        assertNotNull(booking);
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByIdDesc() {
        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("Select b from Booking b where b.booker.id = :idBooker and b.end < :currentDate order by b.id desc ", Booking.class);
        query.setParameter("idBooker", booking1.getBooker().getId());
        query.setParameter("currentDate", LocalDateTime.of(2022,12,31,11,00));
        Booking booking = query.getSingleResult();
        assertNotNull(booking);
    }

    @Test
    void findAllByBookerCurrent() {
        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("Select b from Booking b where b.booker.id = :idBooker and b.end > :currentDate order by b.id desc ", Booking.class);
        query.setParameter("idBooker", booking1.getBooker().getId());
        query.setParameter("currentDate", LocalDateTime.of(2022, 12,15,10,00));
        Booking booking = query.getSingleResult();
        assertNotNull(booking);
    }
}
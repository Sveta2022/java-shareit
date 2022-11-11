package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(Long userId, LocalDateTime now);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b" +
                    " where b.BOOKER_ID = ?1 and ?2 > b.START_DATE" +
                    "  and ?2 < b.END_DATE order by b.START_DATE ")
    List<Booking> findAllByBookerCurrent(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b " +
                    " where b.BOOKER_ID = ?1 and b.STATUS = ?2 " +
                    "order by b.START_DATE desc ")
    List<Booking> findAllByBookerIdState(Long userId, String status);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b " +
                    "join ITEMS i on i.ID = b.ITEM_ID " +
                    " where i.OWNER_ID = ?1 " +
                    "order by b.START_DATE desc ")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long userId);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b " +
                    "join ITEMS i on i.ID = b.ITEM_ID " +
                    " where i.OWNER_ID = ?1 and b.START_DATE<?2 and b.END_DATE>?2 " +
                    "order by b.START_DATE desc ")
    List<Booking> findAllByOwnerIdCurrent(Long userId, LocalDateTime now);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b " +
                    "join ITEMS i on i.ID = b.ITEM_ID " +
                    " where i.OWNER_ID = ?1 and b.START_DATE<?2 and b.END_DATE<?2 " +
                    "order by b.START_DATE desc ")
    List<Booking> findAllByOwnerIdStatePast(Long userId, LocalDateTime now);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b " +
                    "join ITEMS i on i.ID = b.ITEM_ID " +
                    " where i.OWNER_ID = ?1 and b.START_DATE>?2 and b.END_DATE>?2 " +
                    "order by b.START_DATE desc ")
    List<Booking> findAllByOwnerIdStateFuture(Long userId, LocalDateTime now);

    @Query(nativeQuery = true,
            value = "select * from BOOKINGS as b " +
                    "join ITEMS i on i.ID = b.ITEM_ID " +
                    " where i.OWNER_ID = ?1 and b.STATUS = ?2 " +
                    "order by b.START_DATE desc ")
    List<Booking> findAllByOwnerIdState(Long userId, String status);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findOneByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);
}

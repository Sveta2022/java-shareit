package ru.practicum.shareit.request.dao;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestStorage extends JpaRepository<ItemRequest, Long> {

    @Query(nativeQuery = true,
            value = "select * from REQUESTS as r " +
                    "where r.REQUESTOR_ID = ?1 " +
                    "order by r.CREATED desc ")
    List<ItemRequest> findByUserId(Long userId);


    @Query(nativeQuery = true,
            value = "select * from REQUESTS as r " +
                    "join ITEMS i on r.ID = i.REQUEST_ID " +
                    "where r.REQUESTOR_ID != ?1 " +
                    "order by r.CREATED desc ")
    List<ItemRequest> findAllByUserID(Long userId, PageRequest pageRequest);
}

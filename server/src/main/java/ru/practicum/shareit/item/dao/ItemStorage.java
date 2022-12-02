package ru.practicum.shareit.item.dao;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findByDescriptionContainingIgnoreCase(String text, PageRequest pageRequest);

    @Query(nativeQuery = true,
            value = "select * from ITEMS as i " +
                    "where i.REQUEST_ID =?1 ")
    List<Item> findByIdRequest(Long idRequest);
}

package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long userId);
    Page<Item> findByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);
    @Query(value = " select * from items as i " +
            " where lower(i.name) like lower(concat('%',?1,'%')) or lower(i.description) like lower(concat('%',?1,'%'))" +
            " and is_available=true ", nativeQuery = true)
    Page<Item> search(String text,Pageable pageable);
    List<Item> findByRequestIdOrderByIdAsc(Long requestId);
    List<Item> findByRequestIdOrderByIdAsc(Long requestId, Pageable pageable);
}

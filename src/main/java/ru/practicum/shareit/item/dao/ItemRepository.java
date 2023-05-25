package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query(value = " select * from items as i " +
            " where lower(i.name) like lower(concat('%',?1,'%')) or lower(i.description) like lower(concat('%',?1,'%'))" +
            " and is_available=true ", nativeQuery = true)
    List<Item> search(String text);
}

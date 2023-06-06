package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByRequestor_IdOrderByIdAsc(Long userId, Pageable pageable);

    Page<ItemRequest> findAllByRequestor_Id(Long userId, Pageable pageable);

    List<ItemRequest> findByRequestorIdOrderByIdAsc(Long userId);

    Page<ItemRequest> findAll(Pageable pageable);

    Page<ItemRequest> findByRequestor_IdNot(Long userId, Pageable pageable);
}

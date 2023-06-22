package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByItem_Owner_Id(Long userId);

    List<Booking> findByItem_Owner_Id(Long userId, Pageable pageable);

    List<Booking> findByItem_idOrderByStartAsc(Long itemId, Pageable pageable);

    List<Booking> findByItem_idOrderByStartAsc(Long itemId);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime start, Pageable pageable);
}

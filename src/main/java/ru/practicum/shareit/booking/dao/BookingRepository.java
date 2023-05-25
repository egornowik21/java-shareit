package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId);
    List<Booking> findByBooker_Id(Long bookerId,Sort sort);
    List<Booking> findByItem_Owner_Id(Long userId);
    List<Booking> findByItem_Owner_Id(Long userId,Sort sort);
    List<Booking> findByItem_idOrderByStartAsc(Long itemId);
    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,LocalDateTime end, Sort sort);
    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);
    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime start, Sort sort);
    List<Booking>findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,LocalDateTime end, Sort sort);
    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);
    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime start, Sort sort);
}

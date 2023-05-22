package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = " select * from bookings where booker_id = ?1", nativeQuery = true)
    List<Booking> findByBookerIdOrderByIdAsc(Long userId);

    @Query(value = " select * from bookings as b join items as i on " +
            "b.item_id = i.id " +
            "where i.owner_id = ?1", nativeQuery = true)
    List<Booking> findByOwnerId(Long userId);
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBooker_IdTest() {
        User user = new User();
        user.setEmail("demo-user2@email.com");
        user.setName("name2");
        User user2 = new User();
        user.setEmail("demo-user3@email.com");
        user.setName("name3");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("demodesk2");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        Item item = new Item();
        item.setRequest(itemRequest);
        item.setName("item");
        item.setDescription("test");
        item.setAvailable(Boolean.TRUE);
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setBooker(user2);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(3));
        booking.setStatus(Status.WAITING);
        Assertions.assertNull(booking.getId());
        userRepository.save(user);
        requestRepository.save(itemRequest);
        itemRepository.save(item);
        List<Booking> bookingList = bookingRepository.findByBooker_Id(user2.getId());
        Assertions.assertNotNull(bookingList.size());
    }
}

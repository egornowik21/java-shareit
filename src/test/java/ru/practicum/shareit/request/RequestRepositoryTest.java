package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;



    @Test
    void testfindByRequestorIdNot() {
        User user = new User();
        user.setEmail("demo-user4@email.com");
        user.setName("name4");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("demodesk4");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        Assertions.assertNull(itemRequest.getId());
        userRepository.save(user);
        requestRepository.save(itemRequest);
        List<ItemRequest> returnList = requestRepository.findByRequestor_IdNot(user.getId(), Pageable.unpaged()).toList();
        Assertions.assertNotNull(returnList.size());
    }
}

package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testsearchItem() {
        User user = new User();
        user.setEmail("demo-user2@email.com");
        user.setName("name2");
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
        Assertions.assertNotNull(item.getId());
        userRepository.save(user);
        requestRepository.save(itemRequest);
        itemRepository.save(item);
        List<Item> returnList =itemRepository.search("item",Pageable.unpaged()).toList();
        Assertions.assertNotNull(returnList.size());
    }
}

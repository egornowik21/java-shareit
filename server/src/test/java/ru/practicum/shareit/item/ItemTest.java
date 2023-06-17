package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ItemTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Item item1, item2;

    @BeforeEach
    public void beforeEach() {
        item1 = new Item(null, "бензопила", "все люди делятся на две половины", true,
                null, null);
        item2 = new Item(null, "кот", "много есть и спит", true, null, null);

        user = new User(null, "ivanov", "ivanov@gmail.com");
    }

    @Test
    @DisplayName("Тест на добавление и получение всех предметов конкретного юзера")
    void findAllByOwnerIdTest() {
        ResponseEntity<User> responseUser = restTemplate.postForEntity("/users", user, User.class);
        User newUser = userRepository.findById(Objects.requireNonNull(responseUser.getBody()).getId()).get();

        assertEquals(responseUser.getBody().getName(), user.getName());
        assertEquals(responseUser.getBody().getEmail(), user.getEmail());
        assertEquals(newUser.getId(), responseUser.getBody().getId());
        assertEquals(newUser.getName(), responseUser.getBody().getName());

        item1.setOwner(responseUser.getBody());
        item2.setOwner(responseUser.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", responseUser.getBody().getId().toString());
        getPostResponseWithHeader(item1, responseUser.getBody().getId().toString());
        getPostResponseWithHeader(item2, responseUser.getBody().getId().toString());

        Item[] itemsFromController = restTemplate
                .exchange("/items", HttpMethod.GET, new HttpEntity<Object>(headers), Item[].class).getBody();

        List<Item> itemsFromDB = itemRepository
                .findAllByOwnerId(responseUser.getBody().getId(), PageRequest.of(0, 10)).toList();

        assert itemsFromController != null;
        assertEquals(itemsFromController.length, 2);
        assertEquals(itemsFromDB.size(), 2);

        for (int i = 0; i < itemsFromDB.size(); i++) {
            assertEquals(itemsFromController[i].getId(), itemsFromDB.get(i).getId());
            assertEquals(itemsFromController[i].getName(), itemsFromDB.get(i).getName());
            assertEquals(itemsFromController[i].getDescription(), itemsFromDB.get(i).getDescription());
        }
    }

    private void getPostResponseWithHeader(Item item, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        restTemplate.exchange("/items", HttpMethod.POST, entity, Item.class);
    }

    private ResponseEntity<Item> getPatchResponseWithHeader(Item item, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        return restTemplate.exchange("/items/" + item.getId(), HttpMethod.PATCH, entity, Item.class);
    }

    private ResponseEntity<Item[]> getGetResponseWithHeader(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        return restTemplate.exchange("/items", HttpMethod.GET, new HttpEntity<Object>(headers), Item[].class);
    }

    private ResponseEntity<Item[]> getGetResponseWithHeaderWithRequest(String userId, String request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        return restTemplate.exchange("/items/search?text=" + request, HttpMethod.GET,
                new HttpEntity<Object>(headers), Item[].class);
    }
}

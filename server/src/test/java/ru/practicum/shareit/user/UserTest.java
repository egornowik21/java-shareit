package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    private User user1, user2;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "Name1", "Name1@gmail.com");
        user2 = new User(null, "Name2", "Name2@gmail.com");
    }

    @Test
    void findAllTest() {
        restTemplate.postForEntity("/users", user1, User.class);
        restTemplate.postForEntity("/users", user2, User.class);

        HttpHeaders headers = new HttpHeaders();
        User[] usersFromController = restTemplate
                .exchange("/users", HttpMethod.GET, new HttpEntity<Object>(headers),
                        User[].class).getBody();
        List<User> usersFromDB = userRepository.findAll();

        assert usersFromController != null;
        assertEquals(usersFromController.length, 2);
        assertEquals(usersFromDB.size(), 2);

        for (int i = 0; i < usersFromDB.size(); i++) {
            System.out.println(usersFromDB.get(i).getName() + usersFromDB.get(i).getEmail());
            assertEquals(usersFromController[i].getId(), usersFromDB.get(i).getId());
            assertEquals(usersFromController[i].getName(), usersFromDB.get(i).getName());
            assertEquals(usersFromController[i].getEmail(), usersFromDB.get(i).getEmail());
        }
    }

    private ResponseEntity<Item> getPostResponseWithHeader(Item item, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        return restTemplate.exchange("/items", HttpMethod.POST, entity, Item.class);
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
        return restTemplate.exchange("/items/search?text=" + request, HttpMethod.GET, new HttpEntity<Object>(headers), Item[].class);
    }
}

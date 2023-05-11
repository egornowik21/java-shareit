package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ControllerTests {

    private final UserService userService;

    @Test
    public void testinggetAllUsers() {
        User user = User.builder()
                .name("Egor")
                .email("123@mail.ru")
                .build();
        userService.create(user);
        Assertions.assertThat(userService.getUserById(1L));
    }

}

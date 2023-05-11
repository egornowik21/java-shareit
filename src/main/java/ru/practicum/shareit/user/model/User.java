package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class User {
    Long id;
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @Email(message = "Не является почтовым адресом")
    @NotBlank(message = "Почтовый адрес не может быть пустым")
    String email;
}

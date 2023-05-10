package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.Request;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Item {
    long id;
    @NotBlank(message = "Имя не может быть пустым")
    @NotNull(message = "Имя не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    @NotNull(message = "Описание не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    String description;
    @NotBlank(message = "Статус не может быть пустым")
    Boolean available;
    User owner;
    Request request;
}

package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemDto {
    long id;
    @NotBlank(message = "Имя не может быть пустым")
    @NotNull(message = "Имя не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    @NotNull(message = "Описание не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    String description;
    @NotBlank
    Boolean available;
}

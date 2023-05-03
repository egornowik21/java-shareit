package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UserDto {
    Long id;
    String name;
    @Email(message = "Не является почтовым адресом")
    @NotBlank(message = "Почтовый адрес не может быть пустым")
    @NotNull(message = "Почтовый адрес не может быть пустым")
    String email;
}

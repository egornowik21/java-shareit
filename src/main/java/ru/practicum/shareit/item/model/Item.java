package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Column(name = "name", nullable = false, length = 320)
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Column(name = "description", nullable = false, length = 320)
    String description;
    @Column(name = "is_available",nullable = false)
    @NotNull
    Boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
    User owner;
}

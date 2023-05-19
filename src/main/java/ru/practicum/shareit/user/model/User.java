package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;
    @NotBlank
    @Column(name="name",nullable = false,length = 320)
    String name;
    @Column(name="email",nullable = false,length = 320)
    @NotBlank
    @Email
    String email;
}

package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;

import java.time.LocalDateTime;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}

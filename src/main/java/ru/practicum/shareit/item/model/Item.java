package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;
import org.apache.coyote.Request;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    Request request;
}

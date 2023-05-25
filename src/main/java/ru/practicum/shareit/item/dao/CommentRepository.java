package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByItem_id(Long itemId);
}

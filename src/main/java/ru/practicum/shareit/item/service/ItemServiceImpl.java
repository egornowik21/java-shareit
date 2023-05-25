package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<ItemDtoWithDate> findItemByUserId(Long userId) {
        if (userId == null) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        List<ItemDtoWithDate> returnItemList = new ArrayList<>();
        List<Item> usersItems = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        for (Item item : usersItems) {
            List<CommentDto> commentByUser = commentRepository.findByItem_id(item.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(toList());
            if (item.getOwner().getId().equals(userId)) {
                ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
                List<Booking> itemsBooking = bookingRepository.findByItem_idOrderByStartAsc(item.getId());
                itemDtoWithDate.setComments(commentByUser);
                BookingDtoInput lastBooking = itemsBooking.stream()
                        .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                                && booking.getStart().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::bookingDtoInputId)
                        .max(Comparator.comparing(BookingDtoInput::getStart))
                        .orElse(null);

                BookingDtoInput nextBooking = itemsBooking.stream()
                        .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                                && booking.getStart().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::bookingDtoInputId)
                        .min(Comparator.comparing(BookingDtoInput::getStart))
                        .orElse(null);

                if (lastBooking != null) {
                    itemDtoWithDate.setLastBooking(lastBooking);
                }
                if (nextBooking != null) {
                    itemDtoWithDate.setNextBooking(nextBooking);
                }
                returnItemList.add(itemDtoWithDate);
            }
        }
        return returnItemList;
    }

    @Override
    public ItemDtoWithDate getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        List<CommentDto> commentByUser = commentRepository.findByItem_id(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
        itemDtoWithDate.setComments(commentByUser);
        if (item.getOwner().getId().equals(user.getId())) {
            List<Booking> itemsBooking = bookingRepository.findByItem_idOrderByStartAsc(itemId);

            BookingDtoInput lastBooking = itemsBooking.stream()
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                            && booking.getStart().isBefore(LocalDateTime.now()))
                    .map(BookingMapper::bookingDtoInputId)
                    .max(Comparator.comparing(BookingDtoInput::getStart))
                    .orElse(null);

            BookingDtoInput nextBooking = itemsBooking.stream()
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                            && booking.getStart().isAfter(LocalDateTime.now()))
                    .map(BookingMapper::bookingDtoInputId)
                    .min(Comparator.comparing(BookingDtoInput::getStart))
                    .orElse(null);

            if (lastBooking != null) {
                itemDtoWithDate.setLastBooking(lastBooking);
            }
            if (nextBooking != null) {
                itemDtoWithDate.setNextBooking(nextBooking);
            }
        }
        return itemDtoWithDate;
    }

    @Override
    public List<ItemDto> getAllItems() {
        Collection<Item> itemDtoList = itemRepository.findAll();
        return itemDtoList
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ItemDto patchItem(Long userId, Item item, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        String name = item.getName();
        Boolean status = item.getAvailable();
        String description = item.getDescription();
        if (name == null || name.isEmpty() || name.isBlank()) {
            itemToUpdate.setName(itemToUpdate.getName());
        } else {
            itemToUpdate.setName(name);
        }
        if (status == null) {
            itemToUpdate.setAvailable(itemToUpdate.getAvailable());
        } else {
            itemToUpdate.setAvailable(status);
        }
        if (description == null || description.isEmpty() || description.isBlank()) {
            itemToUpdate.setDescription(itemToUpdate.getDescription());
        } else {
            itemToUpdate.setDescription(description);
        }
        Item newItem = itemRepository.save(itemToUpdate);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto postItemByUser(@Valid Long userId, @Valid ItemDto itemDto) {
        checkItem(ItemMapper.inItemDtoWithoutUser(itemDto));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.inItemDto(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto postCommentByItem(@Valid Long userId, @Valid CommentDto commentDto, Long itemId) {
        if (commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Текст комментария пустой");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Comment> commentByUser = commentRepository.findByItem_id(itemId).stream()
                .filter(comment -> comment.getAuthor().getId().equals(user.getId()))
                .collect(toList());
        if (!commentByUser.isEmpty()) {
            throw new ValidationException("Комментарий уже есть");
        }
        List<Booking> bookingByUser = bookingRepository.findByBooker_Id(user.getId()).stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(Status.APPROVED))
                .collect(toList());
        if (bookingByUser.isEmpty()) {
            throw new ValidationException("Бронирование не найдено");
        }
        Comment comment = CommentMapper.inCommentDto(commentDto, item, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        String query = text.toLowerCase();
        return itemRepository.search(query)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    private void checkItem(Item item) {
        if (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().isBlank()) {
            throw new InvalidEmailException("Неверный формат описания");
        }
        if (item.getName() == null || item.getName().isEmpty() || item.getName().isBlank()) {
            throw new InvalidEmailException("Неверный формат имени");
        }
        if (item.getAvailable() == null) {
            throw new InvalidEmailException("Неверный формат статуса");
        }
    }
}

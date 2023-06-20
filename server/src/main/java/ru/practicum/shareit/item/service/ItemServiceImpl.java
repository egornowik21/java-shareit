package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ItemDtoWithDate> findItemByUserId(Long userId, Integer from, Integer size) {
        if (userId == null) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Pageable pageable = PageRequest.of(from, size);
        return itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable)
                .stream()
                .map(this::setCommenstsToItem)
                .map(this::setBookingToItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithDate getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User user = getUserById(userId);
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
        User user = getUserById(userId);
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
    public ItemDto postItemByUser(Long userId, ItemDto itemDto) {
        checkItem(ItemMapper.inItemDtoWithoutUser(itemDto));
        User user = getUserById(userId);
        Item item = ItemMapper.inItemDto(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto postCommentByItem(Long userId, CommentDto commentDto, Long itemId) {
        if (commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Текст комментария пустой");
        }
        User user = getUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
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
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        String query = text.toLowerCase();
        Pageable pageable = PageRequest.of(from, size);
        return itemRepository.search(query, pageable)
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

    private ItemDtoWithDate setCommenstsToItem(Item item) {
        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        List<CommentDto> commentByUser = commentRepository.findByItem_id(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
        itemDtoWithDate.setComments(commentByUser);
        return itemDtoWithDate;
    }

    private ItemDtoWithDate setBookingToItem(ItemDtoWithDate itemDtoWithDate) {
        List<Booking> itemsBooking = bookingRepository.findByItem_idOrderByStartAsc(itemDtoWithDate.getId());
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
        return itemDtoWithDate;
    }

    private User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return user;
    }
}

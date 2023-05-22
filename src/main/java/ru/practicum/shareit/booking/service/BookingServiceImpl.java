package ru.practicum.shareit.booking.service;

import ch.qos.logback.core.joran.conditional.IfAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Booking postBookingByUser(@Valid Long userId, @Valid BookingDtoInput bookingDtoInput) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDtoInput.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец вещи не может создать бронирование");
        }
        if (bookingDtoInput.getStart() == null || bookingDtoInput.getEnd() == null) {
            throw new ValidationException("Время старта или время окончания пустые");
        }
        if (item.getAvailable() == false) {
            throw new ValidationException("вещь недоступна для аренды");
        }
        if (bookingDtoInput.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время окончания в прошлом");
        }
        if (bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart())) {
            throw new ValidationException("Время окончания в раньше времени начала");
        }
        if (bookingDtoInput.getEnd().isEqual(bookingDtoInput.getStart())) {
            throw new ValidationException("Время старта содержит время окончания");
        }
        if (bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время старта в будущем");
        }
        Booking booking = BookingMapper.bookingDtoEntity(bookingDtoInput, user, item);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    public Booking patchBookingByUser(@Valid Long userId, Long bookingId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Вы не владелец вещи");
        }
        if (booking.getStatus().equals(Status.WAITING) && approved) {
            booking.setStatus(Status.APPROVED);
        } else if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("бронирование уже подверждено");
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }


    public Booking getBookingById(Long bookingId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getBooker().equals(user) || booking.getItem().getOwner().equals(user)) {
            return booking;
        } else {
            throw new NotFoundException("Вы не владелец или не арендатор вещи");
        }
    }

    public List<Booking> getAllBokingsByUser(String state, Long userId) {
        if (state == null) {
            state = State.ALL.toString();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookingByUser = bookingRepository.findByBookerIdOrderByIdAsc(user.getId());
        try {
            List<Booking> returnList = getListBookingByState(State.valueOf(state), bookingByUser);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<Booking> returnList = getListBookingByState(State.valueOf(state), bookingByUser);
        return returnList;
    }

    public List<Booking> getAllBokingsByOwner(String state, Long userId) {
        if (state == null) {
            state = State.ALL.toString();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookingByUser = bookingRepository.findByOwnerId(user.getId());
        try {
            List<Booking> returnList = getListBookingByState(State.valueOf(state), bookingByUser);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<Booking> returnList = getListBookingByState(State.valueOf(state), bookingByUser);
        return returnList;
    }

    private List<Booking> getListBookingByState(State state, List<Booking> bookingByUser) {
        List<Booking> bookingByState = new ArrayList<>();
        switch (state) {
            case ALL:
                bookingByState = bookingByUser.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingByState = bookingByUser.stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingByState = bookingByUser.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingByState = bookingByUser.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingByState = bookingByUser.stream()
                        .filter(booking -> booking.getStatus() == Status.APPROVED)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingByState = bookingByUser.stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED ||
                                booking.getStatus() == Status.REJECTED)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
        }
        return bookingByState;
    }
}

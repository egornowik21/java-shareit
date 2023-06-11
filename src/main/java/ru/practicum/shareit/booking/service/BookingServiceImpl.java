package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto postBookingByUser(Long userId, BookingDtoInput bookingDtoInput) {
        User user = getUserById(userId);
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
        Booking bookingtoReturn = bookingRepository.save(booking);
        return BookingMapper.bookingDto(bookingtoReturn);
    }

    @Transactional
    public BookingDto patchBookingByUser(Long userId, Long bookingId, Boolean approved) {
        User user = getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вы не владелец вещи");
        }
        if (booking.getStatus().equals(Status.WAITING) && approved) {
            booking.setStatus(Status.APPROVED);
        } else if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("бронирование уже подверждено");
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking bookingtoReturn = bookingRepository.save(booking);
        return BookingMapper.bookingDto(bookingtoReturn);
    }


    public BookingDto getBookingById(Long bookingId, Long userId) {
        User user = getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getBooker().equals(user) || booking.getItem().getOwner().equals(user)) {
            return BookingMapper.bookingDto(booking);
        } else {
            throw new NotFoundException("Вы не владелец или не арендатор вещи");
        }
    }

    public List<BookingDto> getAllBokingsByUser(String state, Long userId, Integer from, Integer size) {
        User user = getUserById(userId);
        List<Booking> bookingByState = new ArrayList<>();
        Pageable pageable = PageRequest.of(from/size, size,Sort.by(DESC, "start"));
        switch (state) {
            case "ALL":
                bookingByState = bookingRepository.findByBooker_Id(userId, pageable);
                break;
            case "WAITING":
                bookingByState = bookingRepository.findByBooker_Id(userId).stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                bookingByState = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingByState = bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingByState = bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case "REJECTED":
                bookingByState = bookingRepository.findByBooker_Id(user.getId()).stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED ||
                                booking.getStatus() == Status.CANCELED)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
        }
        return bookingByState.stream()
                .map(BookingMapper::bookingDto)
                .collect(Collectors.toList());
    }


    public List<BookingDto> getAllBokingsByOwner(String state, Long userId,Integer from, Integer size) {
        User user = getUserById(userId);
        List<Booking> bookingByState = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size,Sort.by(DESC, "start"));
        switch (state) {
            case "ALL":
                bookingByState = bookingRepository.findByItem_Owner_Id(user.getId(), pageable);
                break;
            case "WAITING":
                bookingByState = bookingRepository.findByItem_Owner_Id(user.getId()).stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                bookingByState = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(user.getId(), LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingByState = bookingRepository.findByItem_Owner_IdAndStartIsAfter(user.getId(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingByState = bookingRepository.findByItem_Owner_IdAndEndIsBefore(user.getId(), LocalDateTime.now(), pageable);
                break;
            case "REJECTED":
                bookingByState = bookingRepository.findByItem_Owner_Id(user.getId()).stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED ||
                                booking.getStatus() == Status.CANCELED)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
        }
        return bookingByState.stream()
                .map(BookingMapper::bookingDto)
                .collect(Collectors.toList());
    }
    private User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return user;
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final User user = new User(null, "user", "user@yandex.ru");
    private final User user2 = new User(null, "user2", "user2@yandex.ru");
    private final ItemRequest itemRequest = new ItemRequest(null, "test", user, LocalDateTime.now());
    private final Item item = new Item(1L, "item", "deskitem", Boolean.TRUE, user, null);
    private final Comment comment = new Comment(1L, "comment", user, item, LocalDateTime.now());
    private final Booking booking = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.WAITING);
    @Autowired
    RequestService requestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    @Autowired
    BookingService bookingService;

    @Test
    void createDone() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(booking));
        assertEquals(user2.getName(), bookingDto.getBooker().getName());
        assertEquals(item.getName(), bookingDto.getItem().getName());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }


    @Test
    void createWithPastDateTime() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now(), LocalDateTime.now(), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        assertThrows(ValidationException.class, () -> bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest)));
    }

    @Test
    void createWithFutureDateTime() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusSeconds(2), LocalDateTime.now(), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        assertThrows(ValidationException.class, () -> bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest)));
    }

    @Test
    void createWithBookerId() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        assertThrows(NotFoundException.class, () -> bookingService.postBookingByUser(user.getId(), BookingMapper.bookingDtoInputId(booking)));
    }

    @Test
    void createWithItemNotAvailable() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        Item itemToTest = new Item(1L, "item", "deskitem", Boolean.FALSE, user2, null);
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(itemToTest));
        itemToTest.setId(itemDto.getId());
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusSeconds(2), LocalDateTime.now(), itemToTest, user, Status.WAITING);
        assertThrows(ValidationException.class, () -> bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest)));
    }


    @Test
    void createWithNullEndAndStart() {
        Booking bookingToTest = new Booking(null, null, null, item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        assertThrows(ValidationException.class, () -> bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest)));
    }

    @Test
    void createWithFutureStartTime() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(3), LocalDateTime.now().minusHours(3), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        assertThrows(ValidationException.class, () -> bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest)));
    }

    @Test
    void patchBookingDone() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(booking));
        booking.setId(bookingDto.getId());
        BookingDto patchBookingDto = bookingService.patchBookingByUser(user.getId(), booking.getId(), Boolean.TRUE);
        assertEquals(user2.getName(), patchBookingDto.getBooker().getName());
        assertEquals(item.getName(), patchBookingDto.getItem().getName());
        assertEquals(Status.APPROVED, patchBookingDto.getStatus());
    }

    @Test
    void patchBookingApproved() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.APPROVED);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        bookingToTest.setId(bookingDto.getId());
        BookingDto patchBookingDto = bookingService.patchBookingByUser(user.getId(), bookingToTest.getId(), Boolean.FALSE);
        assertEquals(user2.getName(), patchBookingDto.getBooker().getName());
        assertEquals(item.getName(), patchBookingDto.getItem().getName());
        assertEquals(Status.REJECTED, patchBookingDto.getStatus());
    }

    @Test
    void getAllBookingsByUserTest() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(booking));
        List<BookingDto> returnList = bookingService.getAllBokingsByUser("ALL", user2.getId(), 0, 25);
        assertEquals(1, returnList.size());
    }

    @Test
    void getWaitingBookingsByUserTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByUser("WAITING",user2.getId(),0,25);
        assertEquals(1,returnList.size());
    }

    @Test
    void getRejectedBookingsByUserTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.CANCELED);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByUser("REJECTED",user2.getId(),0,25);
        assertNotNull(returnList.size());
    }
    @Test
    void getFutureBookingsByUserTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByUser("FUTURE",user2.getId(),0,25);
        assertEquals(1,returnList.size());
    }

    @Test
    void getPastBookingsByUserTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusSeconds(6), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByUser("PAST",user2.getId(),0,25);
        assertNotNull(returnList.size());
    }

    @Test
    void getCurrentBookingsByUserTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusSeconds(6), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByUser("CURRENT",user2.getId(),0,25);
        assertNotNull(returnList.size());
    }

    @Test
    void getAllBookingsByOwnerTest() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(booking));
        List<BookingDto> returnList = bookingService.getAllBokingsByOwner("ALL", user.getId(), 0, 25);
        assertEquals(1, returnList.size());
    }

    @Test
    void getWaitingBookingsByOwnerTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByOwner("WAITING",user.getId(),0,25);
        assertEquals(1,returnList.size());
    }

    @Test
    void getRejectedBookingsByOwnerTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.CANCELED);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByOwner("REJECTED",user.getId(),0,25);
        assertNotNull(returnList.size());
    }
    @Test
    void getFutureBookingsByOwnerTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByOwner("FUTURE",user.getId(),0,25);
        assertEquals(1,returnList.size());
    }

    @Test
    void getPastBookingsByOwnerTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusSeconds(6), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByOwner("PAST",user.getId(),0,25);
        assertNotNull(returnList.size());
    }

    @Test
    void getCurrentBookingsByOwnerTest() {
        Booking bookingToTest = new Booking(null, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusSeconds(6), item, user, Status.WAITING);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(bookingToTest));
        List<BookingDto> returnList = bookingService.getAllBokingsByOwner("CURRENT",user.getId(),0,25);
        assertNotNull(returnList.size());
    }

    @Test
    void getBookingById() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(booking));
        BookingDto bookingDtoId = bookingService.getBookingById(bookingDto.getId(), user2.getId());
        assertEquals(bookingDto.getBooker().getName(), bookingDtoId.getBooker().getName());
        assertEquals(bookingDto.getItem().getName(), bookingDtoId.getItem().getName());
        assertEquals(bookingDto.getStatus(), bookingDtoId.getStatus());
    }

    @Test
    void getBookingByIdByOtherUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));
        user2.setId(userDto2.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());
        BookingDto bookingDto = bookingService.postBookingByUser(user2.getId(), BookingMapper.bookingDtoInputId(booking));
        assertThrows(NotFoundException.class,()-> bookingService.getBookingById(bookingDto.getId(), 5L));
    }

}

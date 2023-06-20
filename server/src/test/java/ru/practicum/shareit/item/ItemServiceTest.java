package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    @Autowired
    RequestService requestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @Autowired
    BookingService bookingService;

    private final User user = new User(null, "user", "user@yandex.ru");
    private final User user2 = new User(null, "user2", "user2@yandex.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "test", user, LocalDateTime.now());
    private final ItemRequestDtoInput itemRequestDtoinput = ItemRequestMapper.toItemRequestDtoInput(itemRequest);
    private final ItemRequestDto itemRequestDto = ItemRequestMapper.toRequestDto(itemRequest);

    private final Item item = new Item(1L, "item", "deskitem", Boolean.TRUE, user, null);

    private final Comment comment = new Comment(1L, "comment", user, item, LocalDateTime.now());
    private final CommentDto commentDto = CommentMapper.toCommentDto(comment);

    @Test
    void create() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
    }

    @Test
    void faildCreateItem() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setRequestId(itemRequest.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
        assertThrows(NotFoundException.class, () -> itemService.postItemByUser(user.getId(), itemDto));
    }

    @Test
    void faildCreateItemWithNullName() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        Item item2 = new Item(1L, null, "deskitem", Boolean.TRUE, user, null);
        assertThrows(InvalidEmailException.class, () -> itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item2)));
    }


    @Test
    void getAllItemsByUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setId(itemDto.getId());
        List<ItemDtoWithDate> requestsList = itemService.findItemByUserId(userDto.getId(), 0, 10);
        assertEquals(requestsList.size(), 1);
    }

    @Test
    void getAllItemsWithWrongIdByUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setId(itemDto.getId());
        assertThrows(NotFoundException.class, () -> itemService.findItemByUserId(user2.getId(), 0, 10));
    }

    @Test
    void getAllItems() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setId(itemDto.getId());
        List<ItemDto> requestsList = itemService.getAllItems();
        assertEquals(requestsList.size(), 1);
    }

    @Test
    void getItemById() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setId(itemDto.getId());
        ItemDtoWithDate reusultItem = itemService.getItemById(itemDto.getId(), userDto.getId());
        assertEquals(item.getName(), reusultItem.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
    }

    @Test
    void searchItem() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setId(itemDto.getId());
        List<ItemDto> reusultItemList = itemService.searchItem(itemDto.getName(), 0, 10);
        assertEquals(reusultItemList.size(), 1);
    }

    @Test
    void patchItem() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        itemDto.setId(itemDto.getId());
        ItemDto reusultItem = itemService.patchItem(userDto.getId(), item, itemDto.getId());
        assertEquals(item.getName(), reusultItem.getName());
        assertEquals(item.getDescription(), reusultItem.getDescription());
        assertEquals(item.getOwner().getId(), reusultItem.getOwnerId());
    }

    @Test
    void postFaildComment() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        item.setId(itemDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.create(UserMapper.toUserDto(author));
        author.setId(authorDto.getId());

        assertThrows(NotFoundException.class, () -> itemService.postCommentByItem(item.getId(),
                CommentMapper.toCommentDto(comment), author.getId()));

    }

    @Test
    void postFaildCommentWithouBooking() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.postItemByUser(user.getId(), ItemMapper.toItemDto(item));
        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.create(UserMapper.toUserDto(author));
        comment.setItem(item);
        comment.getItem();
        Comment comment1 = new Comment(1L, "txt", user, item, LocalDateTime.now());
        CommentDto commentDto1 = CommentMapper.toCommentDto(comment1);
        Comment comment2 = CommentMapper.inCommentDto(commentDto1, item, user2);
        author.setId(authorDto.getId());
        assertThrows(ValidationException.class, () -> itemService.postCommentByItem(userDto.getId(), commentDto, itemDto.getId()));

    }


}

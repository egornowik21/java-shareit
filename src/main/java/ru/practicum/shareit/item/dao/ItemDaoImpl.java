package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;
    private final UserDao userDao;
    private long nextId;

    @Override
    public List<ItemDto> findItemByUserId(Long userId) {
        User user = userDao.getUsers().get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователя не существует");
        }
        Collection<Item> itemDtoList = items.values();
        return itemDtoList.stream()
                .filter(item -> user.getId().equals(item.getOwner().getId()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto patchItem(Long userId, Item item, Long itemId) {
        Item itemInput = item;
        User user = userDao.getUsers().get(userId);
        checkItemUser(user);
        Item itemToUpdate;
        boolean updateItem = items.values().stream()
                .filter(itemUpdate -> itemUpdate.equals(itemId) ||
                        itemUpdate.getOwner().getId().equals(user.getId()))
                .anyMatch(itemUpdate -> itemUpdate.getOwner().getId().equals(user.getId()));
        if (!updateItem) {
            throw new NotFoundException("Пользователь у вещи не найден");
        } else {
            itemToUpdate = items.get(itemId);
        }
        String name = itemInput.getName();
        Boolean status = itemInput.getAvailable();
        String description = itemInput.getDescription();
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
        items.put(itemId, itemToUpdate);
        return itemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        Collection<Item> itemDtoList = items.values();
        String textInput = text.toLowerCase();
        return itemDtoList.stream()
                .filter(item -> items.get(item.getId()).getName().toLowerCase().contains(textInput)
                        || items.get(item.getId()).getDescription().toLowerCase().contains(textInput)
                        && items.get(item.getId()).getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems() {
        Collection<Item> itemDtoList = items.values();
        return itemDtoList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Item> getItemsMap() {
        return items;
    }

    @Override
    public ItemDto postItemByUser(Long userId, Item item) {
        checkItem(item);
        User user = userDao.getUsers().get(userId);
        checkItemUser(user);
        item.setId(++nextId);
        item.setOwner(user);
        items.put(item.getId(), item);
        return itemMapper.toItemDto(item);
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

    private void checkItemUser(User user) {
        if (user == null) {
            throw new NotFoundException("Пользователя не существует");
        }
    }
}

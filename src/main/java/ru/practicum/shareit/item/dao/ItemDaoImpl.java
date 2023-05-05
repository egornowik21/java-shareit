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

import java.util.*;

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
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (user == null) {
            throw new NotFoundException("Пользователя не существует");
        }
        for (Item item : items.values()) {
            if (user.getId().equals(item.getOwner().getId())) {
                itemDtoList.add(itemMapper.toItemDto(item));
            }
        }
        return itemDtoList;
    }

    @Override
    public ItemDto patchItem(Long userId, Item item, Long itemId) {
        Item itemInput = item;
        User user = userDao.getUsers().get(userId);
        checkItemUser(user);
        Item itemToUpdate = null;
        for (Long updateItem : items.keySet()) {
            if (updateItem.equals(itemId)) {
                itemToUpdate = items.get(updateItem);
                if (!(itemToUpdate.getOwner().getId().equals(user.getId()))) {
                    throw new NotFoundException("Пользователь у вещи не найден");
                }
            }
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
        ArrayList<ItemDto> itemDtoList = new ArrayList<>();
        String textInput = text.toLowerCase();
        if (text != null && !(text.isEmpty()) && !(text.isBlank())) {
            for (Long itemId : items.keySet()) {
                if (items.get(itemId).getName().toLowerCase().contains(textInput) || items.get(itemId).getDescription().toLowerCase().contains(textInput) &&
                        items.get(itemId).getAvailable()) {
                    itemDtoList.add(itemMapper.toItemDto(items.get(itemId)));
                }
            }
        } else {
            return Collections.EMPTY_LIST;
        }
        return itemDtoList;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems() {
        ArrayList<ItemDto> itemDtoList = new ArrayList<>();
        for (Long itemId : items.keySet()) {
            itemDtoList.add(itemMapper.toItemDto(items.get(itemId)));
        }
        return itemDtoList;
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

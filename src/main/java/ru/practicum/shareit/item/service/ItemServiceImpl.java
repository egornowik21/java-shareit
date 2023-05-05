package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    @Override
    public List<ItemDto> findItemByUserId(Long userId) {
        if (userId == null) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        return itemDao.findItemByUserId(userId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (itemId == null || !(itemDao.getItemsMap().containsKey(itemId))) {
            log.error("Вещь с id - {} не существует", itemId);
            throw new NotFoundException("Вещь не найдена");
        }
        return itemDao.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemDao.getAllItems();
    }

    @Override
    public ItemDto patchItem(Long userId, Item item, Long itemId) {
        if (userId == null) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        return itemDao.patchItem(userId, item, itemId);
    }

    @Override
    public ItemDto postItemByUser(@Valid Long userId, @Valid Item item) {
        log.info("Добавлена новая вещь");
        return itemDao.postItemByUser(userId, item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Выполнен поиск по вещам");
        return itemDao.searchItem(text);
    }
}

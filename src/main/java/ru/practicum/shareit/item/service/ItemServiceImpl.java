package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findItemByUserId(Long userId) {
        if (userId == null) {
            log.error("Пользователь с id - {} не существует", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        List<Item> usersItems = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        return usersItems
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("Вещь с id - {} не существует", itemId);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        Collection<Item> itemDtoList = itemRepository.findAll();
        return itemDtoList
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
    public List<ItemDto> searchItem(String text) {
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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

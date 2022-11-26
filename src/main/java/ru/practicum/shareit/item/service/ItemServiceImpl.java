package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AuthorizationUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        });
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);
        log.info("Вещь с id " + savedItem.getId() + " создана");
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(() -> {
            throw new NotFoundException("Вещь с id " + itemDto.getId() + " не найдена");
        });
        if (!item.getOwner().getId().equals(userId)) {
            throw new AuthorizationUserException("Пользователь с id " + userId + " не имеет прав для изменения этой вещи");
        }
        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (Objects.nonNull(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.update(item);
        log.info("Вещь с id " + updatedItem.getId() + " изменена");
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item foundedItemById = itemRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Вещь с id " + id + " не найдена");
        });
        return itemMapper.toDto(foundedItemById);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        List<Item> items = itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
        return itemMapper.toItemDtoList(items);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findAll();
        List<Item> foundedItems = new ArrayList<>();
        for (Item item : items) {
            if (Boolean.TRUE.equals(item.getAvailable())) {
                if (item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase())) {
                    foundedItems.add(item);
                }
            }
        }
        return itemMapper.toItemDtoList(foundedItems);
    }
}

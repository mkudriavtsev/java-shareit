package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto patchItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> searchItems(String text);
}

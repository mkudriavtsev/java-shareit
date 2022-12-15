package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto patchItem(ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long id, Long userId);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}

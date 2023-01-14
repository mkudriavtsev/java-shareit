package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(CreateItemDto itemDto, Long ownerId);

    ItemDto patch(PatchItemDto itemDto, Long ownerId);

    ItemDto getById(Long id, Long userId);

    List<ItemDto> getByOwnerId(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}

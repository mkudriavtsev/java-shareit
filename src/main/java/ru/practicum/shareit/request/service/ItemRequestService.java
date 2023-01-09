package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(CreateItemRequestDto dto, Long userId);

    ItemRequestDto getById(Long id, Long userId);

    List<ItemRequestDto> getOwn(Long userId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);
}

package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(CreateItemRequestDto dto, Long userId);

    ItemRequestDto getItemRequestById(Long id, Long userId);

    List<ItemRequestDto> getOwnItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size);
}

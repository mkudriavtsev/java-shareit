package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto create(@RequestBody CreateItemRequestDto dto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.create(dto, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getById(@PathVariable Long id,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getOwn(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }
}

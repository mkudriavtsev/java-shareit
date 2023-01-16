package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody CreateItemDto dto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.create(dto, ownerId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patch(@RequestBody PatchItemDto itemDto,
                         @RequestHeader("X-Sharer-User-Id") Long ownerId,
                         @PathVariable Long id) {
        itemDto.setId(id);
        return itemService.patch(itemDto, ownerId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long id,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createComment(commentDto, id, userId);
    }
}

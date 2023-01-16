package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Validated({ValidationGroup.OnCreate.class}) @RequestBody CreateItemDto dto,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.create(ownerId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(
            @Validated({ValidationGroup.OnUpdate.class}) @RequestBody PatchItemDto dto,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long id) {
        return itemClient.patch(id, ownerId, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createComment(id, userId, commentDto);
    }
}

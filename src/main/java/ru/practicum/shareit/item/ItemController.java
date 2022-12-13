package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Validated({ValidationGroup.OnCreate.class})
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        ItemDto createdItemDto = itemService.createItem(itemDto, ownerId);
        return ResponseEntity.status(201).body(createdItemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> patchItem(
            @Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long id
    ) {
        itemDto.setId(id);
        ItemDto updatedItemDto = itemService.patchItem(itemDto, ownerId);
        return ResponseEntity.ok().body(updatedItemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        ItemDto foundedItemDto = itemService.getItemById(id, userId);
        return ResponseEntity.ok().body(foundedItemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        List<ItemDto> foundedItemDtosByOwnerId = itemService.getItemsByOwnerId(ownerId);
        return ResponseEntity.ok().body(foundedItemDtosByOwnerId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        List<ItemDto> foundedItemDtos = itemService.searchItems(text);
        return ResponseEntity.ok().body(foundedItemDtos);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto,
                                                    @PathVariable Long id,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        CommentDto savedCommentDto = itemService.createComment(commentDto, id, userId);
        return ResponseEntity.ok().body(savedCommentDto);
    }
}

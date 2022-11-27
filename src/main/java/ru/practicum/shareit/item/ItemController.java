package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
            @Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto createdItemDto = itemService.createItem(itemDto, userId);
        return ResponseEntity.status(201).body(createdItemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> patchItem(
            @Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id
    ) {
        itemDto.setId(id);
        ItemDto updatedItemDto = itemService.patchItem(itemDto, userId);
        return ResponseEntity.ok().body(updatedItemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        ItemDto foundedItemDto = itemService.getItemById(id);
        return ResponseEntity.ok().body(foundedItemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> foundedItemDtosByUserId = itemService.getItemsByUserId(userId);
        return ResponseEntity.ok().body(foundedItemDtosByUserId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        List<ItemDto> foundedItemDtos = itemService.searchItems(text);
        return ResponseEntity.ok().body(foundedItemDtos);
    }
}

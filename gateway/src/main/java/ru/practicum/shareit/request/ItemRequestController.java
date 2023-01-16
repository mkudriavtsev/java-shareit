package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateItemRequestDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.create(userId, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Min(0) @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Min(1) @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return itemRequestClient.getAll(userId, from, size);
    }
}

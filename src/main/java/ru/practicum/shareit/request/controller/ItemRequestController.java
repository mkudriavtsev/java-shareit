package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto createItemRequest(@Valid @RequestBody CreateItemRequestDto dto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.createItemRequest(dto, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getItemRequestById(@PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0", required = false) @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10", required = false) @Min(1) Integer size) {
        return itemRequestService.getAllItemRequestsPaginated(userId, from, size);
    }
}

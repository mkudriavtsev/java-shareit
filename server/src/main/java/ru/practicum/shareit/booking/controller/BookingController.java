package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody CreateBookingDto dto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(dto, userId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto setApprove(@PathVariable Long id,
                                 @RequestParam Boolean approved,
                                 @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.setApprove(id, approved, ownerId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getById(@PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllForUserByState(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getAllForUserByState(GetBookingRequest.of(userId, state, from, size));
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllForOwnerByState(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.getAllForOwnerByState(GetBookingRequest.of(ownerId, state, from, size));
    }
}

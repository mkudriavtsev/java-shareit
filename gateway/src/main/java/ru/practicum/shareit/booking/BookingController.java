package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateBookingDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking {}, userId={}", dto, userId);
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> setApprove(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingClient.setApprove(id, approved, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking {}, userId={}", id, userId);
        return bookingClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUserByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all", required = false) String stateParam,
            @Min(0) @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Min(1) @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllForUserByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForOwnerByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all", required = false) String stateParam,
            @Min(0) @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Min(1) @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllForOwnerByState(userId, state, from, size);
    }
}

package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@Valid @RequestBody BookingRequestDto dto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto setApprove(@PathVariable Long id,
                                         @RequestParam Boolean approved,
                                         @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.setApprove(id, approved, ownerId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBookingById(@PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllBookingsForUserByState(
            @RequestParam(required = false, defaultValue = "ALL") RequestState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getAllBookingsForUserByState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllBookingsForOwnerByState(
            @RequestParam(required = false, defaultValue = "ALL") RequestState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.getAllBookingsForOwnerByState(ownerId, state);
    }
}

package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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
    public BookingDto createBooking(@Valid @RequestBody CreateBookingDto dto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(dto, userId);
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
    public BookingDto getBookingById(@PathVariable Long id,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllBookingsForUserByState(
            @RequestParam(required = false, defaultValue = "ALL") RequestState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getAllBookingsForUserByState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllBookingsForOwnerByState(
            @RequestParam(required = false, defaultValue = "ALL") RequestState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.getAllBookingsForOwnerByState(ownerId, state);
    }
}

package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto dto,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingResponseDto createdBookingDto = bookingService.createBooking(dto, userId);
        return ResponseEntity.status(201).body(createdBookingDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponseDto> setApprove(@PathVariable Long id,
                                                         @RequestParam Boolean approved,
                                                         @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        BookingResponseDto updatedBookingDto = bookingService.setApprove(id, approved, ownerId);
        return ResponseEntity.status(200).body(updatedBookingDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingResponseDto bookingResponseDto = bookingService.getBookingById(id, userId);
        return ResponseEntity.status(200).body(bookingResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsForUserByState(
            @RequestParam(required = false, defaultValue = "ALL") RequestState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<BookingResponseDto> dtoList = bookingService.getAllBookingsForUserByState(userId, state);
        return ResponseEntity.status(200).body(dtoList);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsForOwnerByState(
            @RequestParam(required = false, defaultValue = "ALL") RequestState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        List<BookingResponseDto> dtoList = bookingService.getAllBookingsForOwnerByState(ownerId, state);
        return ResponseEntity.status(200).body(dtoList);
    }
}

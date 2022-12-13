package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto dto, Long userId);

    BookingResponseDto setApprove(Long id, Boolean isApprove, Long ownerId);

    BookingResponseDto getBookingById(Long id, Long userId);

    List<BookingResponseDto> getAllBookingsForUserByState(Long userId, RequestState state);

    List<BookingResponseDto> getAllBookingsForOwnerByState(Long ownerId, RequestState state);
}

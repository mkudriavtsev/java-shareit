package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingDto dto, Long userId);

    BookingDto setApprove(Long id, Boolean isApprove, Long ownerId);

    BookingDto getBookingById(Long id, Long userId);

    List<BookingDto> getAllBookingsForUserByState(Long userId, RequestState state);

    List<BookingDto> getAllBookingsForOwnerByState(Long ownerId, RequestState state);
}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingDto dto, Long userId);

    BookingDto setApprove(Long id, Boolean isApprove, Long ownerId);

    BookingDto getBookingById(Long id, Long userId);

    List<BookingDto> getAllBookingsForUserByState(GetBookingRequest request);

    List<BookingDto> getAllBookingsForOwnerByState(GetBookingRequest request);
}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(CreateBookingDto dto, Long userId);

    BookingDto setApprove(Long id, Boolean isApprove, Long ownerId);

    BookingDto getById(Long id, Long userId);

    List<BookingDto> getAllForUserByState(GetBookingRequest request);

    List<BookingDto> getAllForOwnerByState(GetBookingRequest request);
}

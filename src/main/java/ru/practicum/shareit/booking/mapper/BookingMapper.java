package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Objects;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public abstract class BookingMapper {

    public abstract Booking toEntityFromBookingRequestDto(BookingRequestDto dto);

    public abstract BookingResponseDto toBookingResponseDtoFromEntity(Booking booking);

    public abstract List<BookingResponseDto> toBookingResponseDtoListFromEntityList(List<Booking> bookingList);

    public BookingItemDto toBookingItemDtoFromEntity(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }
        BookingItemDto dto = new BookingItemDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }
}

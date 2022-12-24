package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Objects;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public abstract class BookingMapper {

    public abstract Booking toEntityFromCreateBookingDto(CreateBookingDto dto);

    public abstract BookingDto toBookingDtoFromEntity(Booking booking);

    public abstract List<BookingDto> toBookingDtoListFromEntityList(List<Booking> bookingList);

    public BookingInItemDto toBookingInItemDtoFromEntity(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }
        BookingInItemDto dto = new BookingInItemDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }
}

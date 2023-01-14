package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toEntity(CreateBookingDto dto);

    BookingDto toBookingDto(Booking booking);

    List<BookingDto> toBookingDtoList(List<Booking> bookingList);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingInItemDto toBookingInItemDto(Booking booking);
}

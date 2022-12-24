package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;
}

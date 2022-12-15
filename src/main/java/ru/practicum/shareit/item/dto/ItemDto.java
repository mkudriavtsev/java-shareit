package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {

    private Long id;

    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String name;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    private String description;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    private Boolean available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;


}

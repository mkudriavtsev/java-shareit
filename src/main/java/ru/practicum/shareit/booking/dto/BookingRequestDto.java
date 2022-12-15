package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    @NotNull
    private Long itemId;

    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}

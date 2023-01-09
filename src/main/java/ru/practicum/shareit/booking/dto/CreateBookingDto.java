package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class CreateBookingDto {

    @NotNull
    private Long itemId;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}

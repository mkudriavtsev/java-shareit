package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.controller.BookingState;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetBookingRequest {
    private Long userId;
    private BookingState state;
    private Integer from;
    private Integer size;
}

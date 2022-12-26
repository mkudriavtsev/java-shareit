package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.controller.RequestState;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetBookingRequest {
    private Long userId;
    private RequestState state;
    private Integer from;
    private Integer size;
}

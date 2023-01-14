package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemInRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
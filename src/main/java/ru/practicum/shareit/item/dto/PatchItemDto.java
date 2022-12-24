package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class PatchItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}

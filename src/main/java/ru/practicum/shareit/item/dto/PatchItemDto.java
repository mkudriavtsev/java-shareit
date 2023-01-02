package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PatchItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}

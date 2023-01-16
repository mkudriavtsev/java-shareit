package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.NotBlankIfNotNull;
import ru.practicum.shareit.validation.ValidationGroup;

@Data
@Builder
@AllArgsConstructor
public class PatchItemDto {
    private Long id;
    @NotBlankIfNotNull(groups = ValidationGroup.OnUpdate.class)
    private String name;
    @NotBlankIfNotNull(groups = ValidationGroup.OnUpdate.class)
    private String description;
    private Boolean available;
}

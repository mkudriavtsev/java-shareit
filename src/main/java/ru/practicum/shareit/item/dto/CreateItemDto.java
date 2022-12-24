package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateItemDto {

    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String name;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    private String description;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    private Boolean available;

    private Long requestId;
}

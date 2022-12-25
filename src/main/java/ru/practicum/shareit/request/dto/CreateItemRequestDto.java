package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateItemRequestDto {
    @NotNull
    private String description;
}

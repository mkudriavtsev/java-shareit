package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private Long id;

    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String name;

    @Email
    @NotNull(groups = ValidationGroup.OnCreate.class)
    private String email;
}

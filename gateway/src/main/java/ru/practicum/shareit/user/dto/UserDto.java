package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private Long id;

    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String name;

    @Email(groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class})
    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String email;
}

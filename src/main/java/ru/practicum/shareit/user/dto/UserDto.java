package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String name;

    @Email
    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String email;
}

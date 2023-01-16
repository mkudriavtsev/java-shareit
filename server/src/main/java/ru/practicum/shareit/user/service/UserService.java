package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto patch(UserDto userDto);

    UserDto getById(Long id);

    List<UserDto> getAll();

    void deleteById(Long id);

    void checkUserExist(Long userId);
}

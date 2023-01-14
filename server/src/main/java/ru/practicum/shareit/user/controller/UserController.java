package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto patch(@PathVariable Long id,
                         @RequestBody UserDto userDto) {
        userDto.setId(id);
        return userService.patch(userDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }
}

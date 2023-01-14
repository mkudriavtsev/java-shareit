package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationGroup;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Validated({ValidationGroup.OnCreate.class}) @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(
            @PathVariable Long id,
            @Validated({ValidationGroup.OnUpdate.class}) @RequestBody UserDto userDto) {
        return userClient.patch(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        return userClient.deleteById(id);
    }
}

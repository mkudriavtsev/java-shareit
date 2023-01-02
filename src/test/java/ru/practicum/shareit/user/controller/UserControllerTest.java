package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createUser_whenInvoked_thenStatusIsCreatedAndReturnedUserDto() {
        UserDto dto = getTestUserDto();
        when(userService.createUser(dto)).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void patchUser_whenInvoked_thenStatusIsOkAndReturnedUserDto() {
        Long userId = 1L;
        UserDto dto = getTestUserDto();
        when(userService.patchUser(dto)).thenReturn(dto);

        mockMvc.perform(patch("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserFound_thenReturnedUserDto() {
        Long userId = 1L;
        UserDto dto = getTestUserDto();
        when(userService.getUserById(userId)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserNotFound_thenStatusNotFound() {
        Long userId = 0L;
        String errorMessage = "User with id " + userId + " not found";
        when(userService.getUserById(userId)).thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersFound_thenReturnedListOfUsersDto() {
        List<UserDto> dtoList = List.of(getTestUserDto());
        when(userService.getAllUsers()).thenReturn(dtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void deleteUserById_whenUserFound_thenStatusIsOk() {
        Long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userService).deleteUserById(userId);
    }

    UserDto getTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("jdoe@mail.com")
                .build();
    }
}

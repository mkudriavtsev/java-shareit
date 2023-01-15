package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient userClient;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenStatusIsCreatedAndReturnedUserDto() {
        UserDto dto = getTestUserDto();
        ResponseEntity<Object> response = ResponseEntity.status(201).body(dto);
        when(userClient.create(dto)).thenReturn(response);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void patch_whenInvoked_thenStatusIsOkAndReturnedUserDto() {
        long userId = 1L;
        UserDto dto = getTestUserDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(userClient.patch(userId, dto)).thenReturn(response);

        mockMvc.perform(patch("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getById_whenUserFound_thenReturnedUserDto() {
        long userId = 1L;
        UserDto dto = getTestUserDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(userClient.getById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotFound_thenStatusNotFound() {
        long userId = 0L;
        String errorMessage = "User with id " + userId + " not found";
        ResponseEntity<Object> response = ResponseEntity.status(404).body(new ErrorResponse(errorMessage));
        when(userClient.getById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @SneakyThrows
    @Test
    void getAll_whenUsersFound_thenReturnedListOfUsersDto() {
        List<UserDto> dtoList = List.of(getTestUserDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(userClient.getAll()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void deleteById_whenUserFound_thenStatusIsOk() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).build();
        when(userClient.deleteById(userId)).thenReturn(response);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userClient).deleteById(userId);
    }

    UserDto getTestUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("jdoe@mail.com");
        return dto;
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}

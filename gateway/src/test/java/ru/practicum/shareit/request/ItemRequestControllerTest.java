package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnedSavedRequestDto() {
        long userId = 1L;
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("TestDescription");
        ItemRequestDto savedDto = getRequestDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(savedDto);
        when(itemRequestClient.create(userId, requestDto)).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void getById_whenRequestFound_thenReturnedRequestDto() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDto dto = getRequestDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemRequestClient.getById(requestId, userId)).thenReturn(response);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getOwn_whenRequestsFound_thenReturnedListOfRequestDtos() {
        long userId = 1L;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemRequestClient.getOwn(userId)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getAll_whenRequestsFound_thenReturnedListOfRequestDtos() {
        long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemRequestClient.getAll(userId, from, size)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    ItemRequestDto getRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("TestDescription");
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

}

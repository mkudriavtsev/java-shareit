package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

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
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createItemRequest_whenInvoked_thenReturnedSavedRequestDto() {
        Long userId = 1L;
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("TestDescription");
        ItemRequestDto savedDto = getRequestDto();
        when(itemRequestService.createItemRequest(requestDto, userId)).thenReturn(savedDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void getItemRequestById_whenRequestFound_thenReturnedRequestDto() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto dto = getRequestDto();
        when(itemRequestService.getItemRequestById(requestId, userId)).thenReturn(dto);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getOwnItemRequests_whenRequestsFound_thenReturnedListOfRequestDtos() {
        Long userId = 1L;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        when(itemRequestService.getOwnItemRequests(userId)).thenReturn(dtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getAllItemRequests_whenRequestsFound_thenReturnedListOfRequestDtos() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        when(itemRequestService.getAllItemRequests(userId, from, size)).thenReturn(dtoList);

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

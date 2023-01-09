package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AuthorizationUserException;
import ru.practicum.shareit.exception.NoBookingInPastException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenStatusIsCreatedAndReturnedItemDto() {
        CreateItemDto createItemDto = getCreateItemDto();
        Long ownerId = 1L;
        ItemDto savedDto = getItemDto();
        when(itemService.create(createItemDto, ownerId)).thenReturn(savedDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void patch_whenItemFound_thenReturnUpdatedItemDto() {
        PatchItemDto patchItemDto = getPatchItemDto();
        ItemDto updatedItemDto = getUpdatedItemDto();
        Long itemId = 1L;
        Long ownerId = 1L;
        when(itemService.patch(patchItemDto, ownerId)).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedItemDto)));
    }

    @SneakyThrows
    @Test
    void patch_whenNotOwnerChanges_thenStatusIsForbidden() {
        PatchItemDto patchItemDto = getPatchItemDto();
        Long itemId = 1L;
        Long ownerId = 2L;
        String error = "User with id " + ownerId + " has no rights to change this item";
        when(itemService.patch(patchItemDto, ownerId)).thenThrow(new AuthorizationUserException(error));

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @SneakyThrows
    @Test
    void getById_whenItemFound_thenReturnedItemDto() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto dto = getItemDto();
        dto.setId(1L);
        when(itemService.getById(itemId, userId)).thenReturn(dto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getByOwnerId() {
        Long ownerId = 1L;
        List<ItemDto> dtoList = List.of(getItemDto());
        when(itemService.getByOwnerId(ownerId)).thenReturn(dtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void search_whenItemsFound_thenReturnedListOfItemDtos() {
        String text = "Test";
        List<ItemDto> dtoList = List.of(getItemDto());
        when(itemService.searchItems(text)).thenReturn(dtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void createComment_whenInvoked_thenReturnedSavedCommentDto() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto dto = getCommentDto();
        when(itemService.createComment(dto, itemId, userId)).thenReturn(dto);

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void createComment_withoutFinishedBookings_thenStatusIsBadRequest() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto dto = getCommentDto();
        String error = "You can`t add comment with 0 finished bookings";
        when(itemService.createComment(dto, itemId, userId)).thenThrow(new NoBookingInPastException(error));

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(error)));
    }

    CreateItemDto getCreateItemDto() {
        return CreateItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getItemDto() {
        return ItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    PatchItemDto getPatchItemDto() {
        return PatchItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getUpdatedItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    CommentDto getCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");
        return commentDto;
    }
}

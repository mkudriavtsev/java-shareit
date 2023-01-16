package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

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
    private ItemClient itemClient;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenStatusIsCreatedAndReturnedItemDto() {
        CreateItemDto createItemDto = getCreateItemDto();
        long ownerId = 1L;
        ItemDto savedDto = getItemDto();
        ResponseEntity<Object> response = ResponseEntity.status(201).body(savedDto);
        when(itemClient.create(ownerId, createItemDto)).thenReturn(response);

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
        long itemId = 1L;
        long ownerId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).body(updatedItemDto);
        when(itemClient.patch(itemId, ownerId, patchItemDto)).thenReturn(response);

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
        long itemId = 1L;
        long ownerId = 2L;
        String error = "User with id " + ownerId + " has no rights to change this item";
        ResponseEntity<Object> response = ResponseEntity.status(403).body(new ErrorResponse(error));
        when(itemClient.patch(itemId, ownerId, patchItemDto)).thenReturn(response);

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
        long itemId = 1L;
        long userId = 1L;
        ItemDto dto = getItemDto();
        dto.setId(1L);
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemClient.getById(itemId, userId)).thenReturn(response);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getByOwnerId() {
        long ownerId = 1L;
        List<ItemDto> dtoList = List.of(getItemDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemClient.getByOwnerId(ownerId)).thenReturn(response);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void search_whenItemsFound_thenReturnedListOfItemDtos() {
        long userId = 1L;
        String text = "Test";
        List<ItemDto> dtoList = List.of(getItemDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemClient.searchItems(userId, text)).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void createComment_whenInvoked_thenReturnedSavedCommentDto() {
        long itemId = 1L;
        long userId = 1L;
        CommentDto dto = getCommentDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemClient.createComment(itemId, userId, dto)).thenReturn(response);

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
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

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}

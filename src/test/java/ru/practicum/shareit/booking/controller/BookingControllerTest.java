package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ItemUnavailableException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void create_whenCorrectCreateBookingDto_thenReturnedSavedBookingDto() {
        CreateBookingDto createBookingDto = getCreateBookingDto();
        BookingDto savedBookingDto = getBookingDto();
        savedBookingDto.setStart(createBookingDto.getStart());
        savedBookingDto.setEnd(createBookingDto.getEnd());
        Long userId = 1L;
        when(bookingService.create(createBookingDto, userId)).thenReturn(savedBookingDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedBookingDto)));
    }

    @SneakyThrows
    @Test
    void create_whenItemUnavailable_thenStatusIsBadRequest() {
        CreateBookingDto createBookingDto = getCreateBookingDto();
        Long userId = 1L;
        String error = "Item with id " + createBookingDto.getItemId() + " not available";
        when(bookingService.create(createBookingDto, userId)).thenThrow(new ItemUnavailableException(error));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @SneakyThrows
    @Test
    void create_whenStartEqualsEnd_thenStatusIsBadRequest() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        CreateBookingDto createBookingDto = getCreateBookingDto();
        createBookingDto.setStart(date);
        createBookingDto.setEnd(date);
        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenStartAfterEnd_thenStatusIsBadRequest() {
        CreateBookingDto createBookingDto = getCreateBookingDto();
        createBookingDto.setStart(LocalDateTime.now().plusDays(5));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void setApprove_whenApproved_thenReturnedUpdatedBookingDto() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        BookingDto dto = getBookingDto();
        dto.setStatus(Status.APPROVED);
        when(bookingService.setApprove(bookingId, true, ownerId)).thenReturn(dto);

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void setApprove_whenStatusAlreadySet_thenReturnedUpdatedBookingDto() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        String error = "Booking status is already set";
        when(bookingService.setApprove(bookingId, true, ownerId)).thenThrow(new BookingStatusException(error));

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @SneakyThrows
    @Test
    void getById_whenBookingFound_thenReturnedBookingDto() {
        Long bookingId = 1L;
        Long userId = 1L;
        BookingDto dto = getBookingDto();
        when(bookingService.getById(bookingId, userId)).thenReturn(dto);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getAllForUserByState_whenStateIsCorrect_thenReturnedListOfBookingDtos() {
        RequestState state = RequestState.ALL;
        Integer from = 0;
        Integer size = 10;
        Long userId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        when(bookingService.getAllForUserByState(GetBookingRequest.of(userId, state, from, size)))
                .thenReturn(dtoList);

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getAllForUserByState_whenStateIsIncorrect_thenStatusIsBadRequest() {
        String state = "UNKNOWN";
        Integer from = 0;
        Integer size = 10;
        Long userId = 1L;
        String error = "Unknown state: UNKNOWN";

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @SneakyThrows
    @Test
    void getAllForOwnerByState_whenStateIsCorrect_thenReturnedListOfBookingDtos() {
        RequestState state = RequestState.ALL;
        Integer from = 0;
        Integer size = 10;
        Long ownerId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        when(bookingService.getAllForOwnerByState(GetBookingRequest.of(ownerId, state, from, size)))
                .thenReturn(dtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    CreateBookingDto getCreateBookingDto() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(2L));
        dto.setEnd(LocalDateTime.now().plusDays(3L));
        return dto;
    }

    BookingDto getBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(Status.WAITING);
        return dto;
    }
}

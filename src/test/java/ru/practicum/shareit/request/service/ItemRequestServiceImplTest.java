package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Spy
    @InjectMocks
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void createItemRequest_whenInvoked_thenSavedItemRequest() {
        Long userId = 1L;
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("TestDescription");
        User user = getUser();
        ItemRequest requestToSave = itemRequestMapper.toEntity(dto);
        requestToSave.setRequester(user);
        requestToSave.setId(1L);
        ItemRequestDto expectedRequestDto = itemRequestMapper.toDto(requestToSave);
        doAnswer(invocationOnMock -> {
            ItemRequest request = invocationOnMock.getArgument(0, ItemRequest.class);
            request.setId(1L);
            return request;
        }).when(itemRequestRepository).save(any(ItemRequest.class));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ItemRequestDto actualRequestDto = itemRequestService.createItemRequest(dto, userId);

        assertEquals(expectedRequestDto, actualRequestDto);
        verify(itemRequestRepository).save(requestToSave);
    }

    @Test
    void createItemRequest_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("TestDescription");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(dto, userId));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestById_whenItemRequestFound_thenReturnItemRequestDto() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        ItemRequest foundedRequest = getItemRequest();
        ItemRequestDto expectedDto = itemRequestMapper.toDto(foundedRequest);
        doNothing().when(userService).checkUserExist(userId);
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(foundedRequest));

        ItemRequestDto actualDto = itemRequestService.getItemRequestById(itemRequestId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        doNothing().when(userService).checkUserExist(userId);
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequestId, userId));
    }

    @Test
    void getOwnItemRequests_whenInvoked_thenReturnListOfOwnItemRequestDtos() {
        Long userId = 1L;
        doNothing().when(userService).checkUserExist(userId);
        List<ItemRequest> foundedItemRequests = List.of(getItemRequest());
        List<ItemRequestDto> expectedDtos = itemRequestMapper.toDtoList(foundedItemRequests);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)).thenReturn(foundedItemRequests);

        List<ItemRequestDto> actualDtos = itemRequestService.getOwnItemRequests(userId);

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getAllItemRequests_whenInvoked_thenReturnListOfAllItemRequestDtos() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequest> foundedItemRequests = List.of(getItemRequest());
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("created").descending());
        Page<ItemRequest> page = new PageImpl<>(foundedItemRequests);
        List<ItemRequestDto> expectedDtos = itemRequestMapper.toDtoList(foundedItemRequests);
        doNothing().when(userService).checkUserExist(userId);
        when(itemRequestRepository.findByRequesterIdIsNot(userId, pageRequest)).thenReturn(page);

        List<ItemRequestDto> actualDtos = itemRequestService.getAllItemRequests(userId, from, size);

        assertEquals(expectedDtos, actualDtos);
    }

    User getUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    ItemRequest getItemRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("TestDescription");
        request.setRequester(getUser());
        request.setCreated(LocalDateTime.now());
        return request;
    }
}

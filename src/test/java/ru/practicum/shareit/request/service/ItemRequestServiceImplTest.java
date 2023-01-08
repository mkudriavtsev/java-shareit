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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private ItemRepository itemRepository;
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
    void create_whenInvoked_thenSavedItemRequest() {
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

        ItemRequestDto actualRequestDto = itemRequestService.create(dto, userId);

        assertEquals(expectedRequestDto, actualRequestDto);
        verify(itemRequestRepository).save(requestToSave);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("TestDescription");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(dto, userId));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getById_whenItemRequestFound_thenReturnItemRequestDto() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        ItemRequest foundedRequest = getItemRequest();
        ItemRequestDto expectedDto = itemRequestMapper.toDto(foundedRequest);
        expectedDto.setItems(Collections.emptyList());
        doNothing().when(userService).checkUserExist(userId);
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(foundedRequest));
        when(itemRepository.findAllByRequestId(itemRequestId)).thenReturn(Collections.emptyList());

        ItemRequestDto actualDto = itemRequestService.getById(itemRequestId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getById_whenItemRequestNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        doNothing().when(userService).checkUserExist(userId);
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(itemRequestId, userId));
    }

    @Test
    void getOwn_whenInvoked_thenReturnListOfOwnItemRequestDtos() {
        Long userId = 1L;
        doNothing().when(userService).checkUserExist(userId);
        List<ItemRequest> foundedItemRequests = List.of(getItemRequest());
        List<Item> items = List.of(getTestItem());
        List<ItemRequestDto> expectedDtos = itemRequestMapper.toDtoList(foundedItemRequests);
        expectedDtos.get(0).setItems(itemMapper.toItemInRequestDtoList(items));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)).thenReturn(foundedItemRequests);
        when(itemRepository.findByRequestIn(foundedItemRequests)).thenReturn(items);

        List<ItemRequestDto> actualDtos = itemRequestService.getOwn(userId);

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getAll_whenInvoked_thenReturnListOfAllItemRequestDtos() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequest> foundedItemRequests = List.of(getItemRequest());
        List<Item> items = List.of(getTestItem());
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("created").descending());
        Page<ItemRequest> page = new PageImpl<>(foundedItemRequests);
        List<ItemRequestDto> expectedDtos = itemRequestMapper.toDtoList(foundedItemRequests);
        expectedDtos.get(0).setItems(itemMapper.toItemInRequestDtoList(items));
        doNothing().when(userService).checkUserExist(userId);
        when(itemRequestRepository.findByRequesterIdIsNot(userId, pageRequest)).thenReturn(page);
        when(itemRepository.findByRequestIn(foundedItemRequests)).thenReturn(items);

        List<ItemRequestDto> actualDtos = itemRequestService.getAll(userId, from, size);

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

    Item getTestItem() {
        return Item.builder()
                .id(1L)
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .request(getItemRequest())
                .build();
    }
}

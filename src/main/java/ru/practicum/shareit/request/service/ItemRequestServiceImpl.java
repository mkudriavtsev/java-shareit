package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(CreateItemRequestDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User with id " + userId + " not found");
        });
        ItemRequest itemRequest = itemRequestMapper.toEntity(dto);
        itemRequest.setRequester(user);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Request with id " + savedItemRequest.getId() + " created");
        return itemRequestMapper.toDto(savedItemRequest);
    }

    @Override
    public ItemRequestDto getById(Long id, Long userId) {
        userService.checkUserExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Request with id " + id + " not found");
        });
        ItemRequestDto dto = itemRequestMapper.toDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(id);
        dto.setItems(itemMapper.toItemInRequestDtoList(items));
        return dto;
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userService.checkUserExist(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> dtos = itemRequestMapper.toDtoList(requests);
        setItemsToRequestDto(requests, dtos);
        return dtos;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        userService.checkUserExist(userId);
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("created").descending());
        Page<ItemRequest> page = itemRequestRepository.findByRequesterIdIsNot(userId, pageRequest);
        List<ItemRequest> requests = page.getContent();
        List<ItemRequestDto> dtos = itemRequestMapper.toDtoList(requests);
        setItemsToRequestDto(requests, dtos);
        return dtos;
    }

    private void setItemsToRequestDto(List<ItemRequest> requests, List<ItemRequestDto> dtos) {
        Map<Long, List<Item>> items = itemRepository.findByRequestIn(requests)
                .stream()
                .collect(groupingBy(item -> item.getRequest().getId(), toList()));
        for (ItemRequestDto dto : dtos) {
            List<Item> itemList = items.get(dto.getId());
            if (Objects.nonNull(itemList)) {
                dto.setItems(itemMapper.toItemInRequestDtoList(itemList));
            } else {
                dto.setItems(Collections.emptyList());
            }
        }
    }
}

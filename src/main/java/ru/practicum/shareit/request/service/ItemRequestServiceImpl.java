package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(CreateItemRequestDto dto, Long userId) {
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
    public ItemRequestDto getItemRequestById(Long id, Long userId) {
        userService.checkUserExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Request with id " + id + " not found");
        });
        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long userId) {
        userService.checkUserExist(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return itemRequestMapper.toDtoList(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        userService.checkUserExist(userId);
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("created").descending());
        Page<ItemRequest> page = itemRequestRepository.findByRequesterIdIsNot(userId, pageRequest);
        List<ItemRequest> itemRequests = page.getContent();
        return itemRequestMapper.toDtoList(itemRequests);
    }
}

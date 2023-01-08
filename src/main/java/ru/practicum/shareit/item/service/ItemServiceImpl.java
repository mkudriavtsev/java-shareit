package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AuthorizationUserException;
import ru.practicum.shareit.exception.NoBookingInPastException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
public class ItemServiceImpl implements ItemService {

    private static final Sort SORT_BY_START_ASC = Sort.by(Sort.Direction.ASC, "start");
    private static final Sort SORT_BY_END_DESC = Sort.by(Sort.Direction.DESC, "end");
    private static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public ItemDto create(CreateItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("User with id " + ownerId + " not found");
        });
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        Long requestId = itemDto.getRequestId();
        if (Objects.nonNull(requestId)) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
                throw new NotFoundException("Request with id " + requestId + " not found");
            });
            item.setRequest(itemRequest);
        }
        Item savedItem = itemRepository.save(item);
        log.info("Item with id {} created", savedItem.getId());
        return itemMapper.toItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto patch(PatchItemDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(() -> {
            throw new NotFoundException("Item with id " + itemDto.getId() + " not found");
        });
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AuthorizationUserException("User with id " + ownerId + " has no rights to change this item");
        }
        itemMapper.updateItem(itemDto, item);
        log.info("Item with id {} updated", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long id, Long userId) {
        Item foundedItemById = itemRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Item with id " + id + " not found");
        });
        ItemDto itemDto = itemMapper.toItemDto(foundedItemById);
        if (foundedItemById.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndEndIsBefore(
                    itemDto.getId(), LocalDateTime.now(), SORT_BY_END_DESC);
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfter(
                    itemDto.getId(), LocalDateTime.now(), SORT_BY_START_ASC);
            itemDto.setLastBooking(bookingMapper.toBookingInItemDto(lastBooking));
            itemDto.setNextBooking(bookingMapper.toBookingInItemDto(nextBooking));
        }
        List<CommentDto> commentDtos = commentMapper.toDtoList(commentRepository.findAllByItemId(id));
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    public List<ItemDto> getByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
        Map<Long, List<Comment>> comments = commentRepository.findByItemIn(items, SORT_BY_CREATED_DESC)
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), toList()));
        Map<Long, List<Booking>> lastBookingsMap = bookingRepository.findByItemInAndEndIsBefore(
                        items, LocalDateTime.now(), SORT_BY_END_DESC)
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
        Map<Long, List<Booking>> nextBookingsMap = bookingRepository.findByItemInAndStartIsAfter(
                        items, LocalDateTime.now(), SORT_BY_START_ASC)
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
        List<ItemDto> itemDtos = itemMapper.toItemDtoList(items);
        for (ItemDto itemDto : itemDtos) {
            List<Booking> lastBookings = lastBookingsMap.get(itemDto.getId());
            List<Booking> nextBookings = nextBookingsMap.get(itemDto.getId());
            List<Comment> commentList = comments.get(itemDto.getId());
            if (Objects.nonNull(lastBookings) && !lastBookings.isEmpty()) {
                itemDto.setLastBooking(bookingMapper.toBookingInItemDto(lastBookings.get(0)));
            }
            if (Objects.nonNull(nextBookings) && !nextBookings.isEmpty()) {
                itemDto.setNextBooking(bookingMapper.toBookingInItemDto(nextBookings.get(0)));
            }
            if (Objects.nonNull(commentList)) {
                itemDto.setComments(commentMapper.toDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> foundedItems = itemRepository.search(text);
        return itemMapper.toItemDtoList(foundedItems);
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        if (bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now())) {
            Comment comment = commentMapper.toEntity(commentDto);
            User author = userRepository.findById(userId).orElseThrow(() -> {
                throw new NotFoundException("User with id " + userId + " not found");
            });
            Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                throw new NotFoundException("Item with id " + itemId + " not found");
            });
            comment.setAuthor(author);
            comment.setItem(item);
            Comment savedComment = commentRepository.save(comment);
            log.info("Comment with id {} created", savedComment.getId());
            return commentMapper.toDto(savedComment);
        } else {
            throw new NoBookingInPastException("You can`t add comment with 0 finished bookings");
        }
    }
}

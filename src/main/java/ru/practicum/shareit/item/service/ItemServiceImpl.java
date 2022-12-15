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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final Sort SORT_BY_START_ASC = Sort.by(Sort.Direction.ASC, "start");
    private static final Sort SORT_BY_END_DESC = Sort.by(Sort.Direction.DESC, "end");

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("User with id " + ownerId + " not found");
        });
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        log.info("Item with id " + savedItem.getId() + " created");
        return itemMapper.toDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto patchItem(ItemDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(() -> {
            throw new NotFoundException("Item with id " + itemDto.getId() + " not found");
        });
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AuthorizationUserException("User with id " + ownerId + " has no rights to change this item");
        }
        itemMapper.updateItemFromDto(itemDto, item);
        Item updatedItem = itemRepository.save(item);
        log.info("Item with id " + updatedItem.getId() + " updated");
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        Item foundedItemById = itemRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Item with id " + id + " not found");
        });
        ItemDto itemDto = itemMapper.toDto(foundedItemById);
        if (foundedItemById.getOwner().getId().equals(userId)) {
            setBookingsToItemDto(itemDto);
        }
        setCommentsToItemDto(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
        List<ItemDto> itemDtos = itemMapper.toItemDtoList(items);
        itemDtos.forEach(this::setBookingsToItemDto);
        itemDtos.forEach(this::setCommentsToItemDto);
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
            comment.setCreated(LocalDateTime.now());
            Comment savedComment = commentRepository.save(comment);
            log.info("Comment with id " + savedComment.getId() + " created");
            return commentMapper.toDto(savedComment);
        } else {
            throw new NoBookingInPastException("You can`t add comment with 0 finished bookings");
        }
    }

    private void setBookingsToItemDto(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndEndIsBefore(
                itemDto.getId(), LocalDateTime.now(), SORT_BY_END_DESC);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfter(
                itemDto.getId(), LocalDateTime.now(), SORT_BY_START_ASC);
        itemDto.setLastBooking(bookingMapper.toBookingItemDtoFromEntity(lastBooking));
        itemDto.setNextBooking(bookingMapper.toBookingItemDtoFromEntity(nextBooking));
    }

    private void setCommentsToItemDto(ItemDto itemDto) {
        List<CommentDto> commentDtos = commentMapper.toDtoList(commentRepository.findAllByItemId(itemDto.getId()));
        itemDto.setComments(commentDtos);
    }
}

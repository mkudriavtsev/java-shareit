package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.InvalidBookingTimeException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto createBooking(CreateBookingDto dto, Long userId) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Item with id " + dto.getItemId() + " not found");
        });
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner of the item cannot create a booking");
        }
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ItemUnavailableException("Item with id " + dto.getItemId() + " not available");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User with id " + userId + " not found");
        });
        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new InvalidBookingTimeException("The end date of the booking cannot be earlier than the start date");
        }
        Booking booking = bookingMapper.toEntity(dto);
        booking.setItem(item);
        booking.setBooker(user);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking with id " + savedBooking.getId() + " created");
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingDto setApprove(Long id, Boolean isApproved, Long ownerId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Booking with id " + id + " not found");
        });
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("User with id " + ownerId + " has no rights to change this item");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new BookingStatusException("Booking status is already set");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking status with id " + updatedBooking.getId() + " changed to " + updatedBooking.getStatus());
        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Booking with id " + id + " not found");
        });
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("User with id " + userId + " has no rights to view this item");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsForUserByState(Long userId, RequestState state) {
        userService.checkUserExist(userId);
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByBookerId(userId, SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfterAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatusIs(
                        userId, Status.WAITING, SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatusIs(
                        userId, Status.REJECTED, SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
        }
        return Collections.emptyList();
    }

    @Override
    public List<BookingDto> getAllBookingsForOwnerByState(Long ownerId, RequestState state) {
        userService.checkUserExist(ownerId);
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByItemOwnerId(ownerId, SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case CURRENT:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case FUTURE:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfter(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case PAST:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsBefore(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case WAITING:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusIs(
                        ownerId, Status.WAITING, SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
            case REJECTED:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusIs(
                        ownerId, Status.REJECTED, SORT_BY_START_DESC);
                return bookingMapper.toBookingDtoList(bookingList);
        }
        return Collections.emptyList();
    }
}

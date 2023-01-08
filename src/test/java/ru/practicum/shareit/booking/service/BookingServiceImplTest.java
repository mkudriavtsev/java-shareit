package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
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
class BookingServiceImplTest {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Spy
    @InjectMocks
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void create_whenInvoked_thenSavedUser() {
        Long userId = 2L;
        CreateBookingDto createBookingDto = getCreateBookingDto();
        Item expectedItem = getItem();
        User expectedUser = getUser();
        expectedUser.setId(2L);
        Booking bookingToSave = bookingMapper.toEntity(createBookingDto);
        bookingToSave.setId(1L);
        bookingToSave.setItem(expectedItem);
        bookingToSave.setBooker(expectedUser);
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(expectedItem));
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        doAnswer(invocationOnMock -> {
            Booking booking = invocationOnMock.getArgument(0, Booking.class);
            booking.setId(1L);
            return booking;
        }).when(bookingRepository).save(any(Booking.class));
        BookingDto expectedBookingDto = bookingMapper.toBookingDto(bookingToSave);
        expectedBookingDto.setId(1L);

        BookingDto actualBookingDto = bookingService.create(createBookingDto, userId);

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(bookingToSave, savedBooking);
    }

    @Test
    void create_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long userId = 2L;
        CreateBookingDto createBookingDto = getCreateBookingDto();
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(createBookingDto, userId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenOwnerCreateBooking_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        CreateBookingDto createBookingDto = getCreateBookingDto();
        Item item = getItem();
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> bookingService.create(createBookingDto, userId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemUnavailable_thenItemUnavailableExceptionThrown() {
        Long userId = 2L;
        CreateBookingDto createBookingDto = getCreateBookingDto();
        Item item = getItem();
        item.setAvailable(Boolean.FALSE);
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ItemUnavailableException.class,
                () -> bookingService.create(createBookingDto, userId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        CreateBookingDto createBookingDto = getCreateBookingDto();
        Item item = getItem();
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class,
                () -> bookingService.create(createBookingDto, userId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void setApprove_whenApproved_thenStatusApprovedSet() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking bookingToUpdate = getBooking();
        BookingDto expectedBookingDto = bookingMapper.toBookingDto(bookingToUpdate);
        expectedBookingDto.setStatus(Status.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        BookingDto actualBookingDto = bookingService.setApprove(bookingId, Boolean.TRUE, ownerId);

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void setApprove_whenRejected_thenStatusRejectedSet() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking bookingToUpdate = getBooking();
        BookingDto expectedBookingDto = bookingMapper.toBookingDto(bookingToUpdate);
        expectedBookingDto.setStatus(Status.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        BookingDto actualBookingDto = bookingService.setApprove(bookingId, Boolean.FALSE, ownerId);

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void setApprove_whenStatusAlreadySet_thenBookingStatusExceptionThrown() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking bookingToUpdate = getBooking();
        bookingToUpdate.setStatus(Status.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(BookingStatusException.class,
                () -> bookingService.setApprove(bookingId, Boolean.TRUE, ownerId));
    }

    @Test
    void setApprove_whenNotOwnerSetStatus_thenNotFoundExceptionThrown() {
        Long bookingId = 1L;
        Long ownerId = 0L;
        Booking bookingToUpdate = getBooking();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(NotFoundException.class,
                () -> bookingService.setApprove(bookingId, Boolean.TRUE, ownerId));
    }

    @Test
    void setApprove_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.setApprove(bookingId, Boolean.TRUE, ownerId));
    }

    @Test
    void getById_whenBookingFoundAndBooker_thenReturnedBooking() {
        Long bookingId = 1L;
        Long userId = 2L;
        Booking foundedBooking = getBooking();
        BookingDto expectedDto = bookingMapper.toBookingDto(foundedBooking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundedBooking));

        BookingDto actualDto = bookingService.getById(bookingId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getById_whenBookingFoundAndItemOwner_thenReturnedBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Booking foundedBooking = getBooking();
        BookingDto expectedDto = bookingMapper.toBookingDto(foundedBooking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundedBooking));

        BookingDto actualDto = bookingService.getById(bookingId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getById_whenBookingFoundAndAnotherUser_thenNotFoundExceptionThrown() {
        Long bookingId = 1L;
        Long userId = 0L;
        Booking foundedBooking = getBooking();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundedBooking));

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
    }

    @Test
    void getById_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Long bookingId = 0L;
        Long userId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
    }

    @ParameterizedTest
    @EnumSource(RequestState.class)
    void getAllForUserByState(RequestState state) {
        GetBookingRequest request = getBookingRequest();
        Long userId = request.getUserId();
        request.setState(state);
        Booking booking = getBooking();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> expectedDtos = bookingMapper.toBookingDtoList(bookings);
        PageRequest pageRequest = PageRequest.of(
                (request.getFrom() / request.getSize()), request.getSize(), SORT_BY_START_DESC);
        Page<Booking> page = new PageImpl<>(bookings);
        doNothing().when(userService).checkUserExist(userId);
        lenient().when(bookingRepository.findByBookerId(userId, pageRequest)).thenReturn(page);
        lenient().when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        lenient().when(bookingRepository.findByBookerIdAndStartIsAfterAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        lenient().when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        lenient().when(bookingRepository.findByBookerIdAndStatusIs(userId, Status.WAITING, pageRequest)).thenReturn(page);
        lenient().when(bookingRepository.findByBookerIdAndStatusIs(userId, Status.REJECTED, pageRequest)).thenReturn(page);

        List<BookingDto> actualDtos = bookingService.getAllForUserByState(request);

        assertEquals(expectedDtos, actualDtos);
    }

    @ParameterizedTest
    @EnumSource(RequestState.class)
    void getAllForOwnerByState(RequestState state) {
        GetBookingRequest request = getBookingRequest();
        Long userId = request.getUserId();
        request.setState(state);
        Booking booking = getBooking();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> expectedDtos = bookingMapper.toBookingDtoList(bookings);
        PageRequest pageRequest = PageRequest.of(
                (request.getFrom() / request.getSize()), request.getSize(), SORT_BY_START_DESC);
        Page<Booking> page = new PageImpl<>(bookings);
        doNothing().when(userService).checkUserExist(userId);
        lenient().when(bookingRepository.findByItemOwnerId(userId, pageRequest)).thenReturn(page);
        lenient().when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        lenient().when(bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        lenient().when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsBefore(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        lenient().when(bookingRepository.findByItemOwnerIdAndStatusIs(userId, Status.WAITING, pageRequest)).thenReturn(page);
        lenient().when(bookingRepository.findByItemOwnerIdAndStatusIs(userId, Status.REJECTED, pageRequest)).thenReturn(page);

        List<BookingDto> actualDtos = bookingService.getAllForOwnerByState(request);

        assertEquals(expectedDtos, actualDtos);
    }

    CreateBookingDto getCreateBookingDto() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(3L));
        return dto;
    }

    Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(Boolean.TRUE);
        item.setOwner(getUser());
        return item;
    }

    User getUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    Booking getBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(3L));
        booking.setItem(getItem());
        booking.setBooker(getUser());
        booking.getBooker().setId(2L);
        return booking;
    }

    GetBookingRequest getBookingRequest() {
        GetBookingRequest request = new GetBookingRequest();
        request.setUserId(1L);
        request.setFrom(0);
        request.setSize(10);
        return request;
    }
}

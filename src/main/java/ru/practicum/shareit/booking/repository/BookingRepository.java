package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfterAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                             LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsBefore(Long bookerId, LocalDateTime start,
                                                               LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStatusIs(Long bookerId, Status status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
                                                                 LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfterAndEndIsAfter(Long ownerId, LocalDateTime start,
                                                                LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsBefore(Long ownerId, LocalDateTime start,
                                                                  LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStatusIs(Long ownerId, Status status, Sort sort);

    Booking findFirstByItemIdAndEndIsBefore(Long itemId, LocalDateTime end, Sort sort);

    Booking findFirstByItemIdAndStartIsAfter(Long itemId, LocalDateTime start, Sort sort);

    Boolean existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(Long itemId, Long bookerId,
                                                                      Status status, LocalDateTime end);
}

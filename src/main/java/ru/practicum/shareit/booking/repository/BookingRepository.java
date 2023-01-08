package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfterAndEndIsAfter(Long bookerId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsBefore(Long bookerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIs(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                 LocalDateTime start,
                                                                 LocalDateTime end,
                                                                 Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfterAndEndIsAfter(Long ownerId,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsBefore(Long ownerId,
                                                                  LocalDateTime start,
                                                                  LocalDateTime end,
                                                                  Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusIs(Long ownerId, Status status, Pageable pageable);

    Booking findFirstByItemIdAndEndIsBefore(Long itemId, LocalDateTime end, Sort sort);

    Booking findFirstByItemIdAndStartIsAfter(Long itemId, LocalDateTime start, Sort sort);

    Boolean existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(Long itemId,
                                                                      Long bookerId,
                                                                      Status status,
                                                                      LocalDateTime end);

    List<Booking> findByItemInAndEndIsBefore(List<Item> items, LocalDateTime end, Sort sort);

    List<Booking> findByItemInAndStartIsAfter(List<Item> items, LocalDateTime start, Sort sort);
}

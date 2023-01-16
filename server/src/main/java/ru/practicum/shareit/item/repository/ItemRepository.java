package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%')))" +
            "and i.available = true")
    List<Item> search(String text);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findByRequestIn(List<ItemRequest> requests);
}

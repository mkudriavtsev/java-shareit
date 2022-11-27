package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemInMemoryRepository implements ItemRepository {

    private final Map<Long, Item> itemMap = new HashMap<>();
    private long currentID;

    private long getNextId() {
        return ++currentID;
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Optional<Item> findById(long id) {
        return itemMap.containsKey(id) ? Optional.of(itemMap.get(id)) : Optional.empty();
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }
}

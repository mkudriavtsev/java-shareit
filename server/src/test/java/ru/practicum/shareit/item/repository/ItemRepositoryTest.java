package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void addTestItemsToDB() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("jdoe@mail.com");
        userRepository.save(user);

        Item itemNotExpect = new Item();
        itemNotExpect.setName("Дрель");
        itemNotExpect.setDescription("Простая дрель");
        itemNotExpect.setAvailable(true);
        itemNotExpect.setOwner(user);
        itemRepository.save(itemNotExpect);

        Item expectedItem = new Item();
        expectedItem.setName("Отвертка");
        expectedItem.setDescription("Аккумуляторная отвертка");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(user);
        itemRepository.save(expectedItem);
    }

    @Test
    void search_whenItemFound_thenReturnListOfItems() {
        String text = "аккУМУляторная";

        List<Item> actualItems = itemRepository.search(text);

        assertEquals(1, actualItems.size());
        assertEquals(actualItems.get(0).getName(), "Отвертка");
        assertEquals(actualItems.get(0).getDescription(), "Аккумуляторная отвертка");
    }

    @Test
    void search_whenItemNotFound_thenReturnEmptyList() {
        String text = "Ножницы";

        List<Item> actualItems = itemRepository.search(text);

        assertTrue(actualItems.isEmpty());
    }
}

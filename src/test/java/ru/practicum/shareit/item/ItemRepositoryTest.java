package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    ItemRepository itemRepository;
    User userOne;
    ItemRequest itemRequestOne;
    User userTwo;
    Item itemOne;
    Item itemTwo;


    @BeforeEach
    void beforeEach() {
        userOne = userRepository.save(new User(1L, "user 1", "user1@email"));
        itemRequestOne = itemRequestRepository.save(new ItemRequest(1L, "item1",
                userTwo, LocalDateTime.now()));
        itemOne = itemRepository.save(new Item(1L, "item 1", "item1",
                true, userOne, itemRequestOne));

        userTwo = userRepository.save(new User(2L, "user 2", "user2@email"));
        itemTwo = itemRepository.save(new Item(2L, "item 2", "item 2 description",
                true, userTwo));

    }

    @Test
    void findAllByOwner() {
        final List<Item> byOwner = itemRepository.findAllByOwner(userOne);

        assertNotNull(byOwner);
        assertEquals(1, byOwner.size());
    }

    @Test
    void findAllByRequestId() {
        final List<Item> byRequestId = itemRepository.findAllByRequestId(2L);
        assertNotNull(byRequestId);
        assertEquals(itemOne, byRequestId.get(0));
    }

    @Test
    void deleteByIdAndOwnerId() {
        itemRepository.deleteByIdAndOwnerId(1L, 1L);
        assertFalse(itemRepository.existsById(1L));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
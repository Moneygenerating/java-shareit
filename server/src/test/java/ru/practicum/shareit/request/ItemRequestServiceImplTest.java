package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@WebServiceClientTest(ItemRequestServiceImpl.class)
class ItemRequestServiceImplTest {

    @Autowired
    ItemRequestService itemRequestService;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;


    private ItemRequest itemRequestOne;
    private ItemRequest itemRequestTwo;
    private ItemRequestDto itemRequestDto;

    private User userOne;
    private User userTwo;

    private ItemRequestDto.Item item;

    private Item itemForUser;

    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "user 1", "user1@email");
        userTwo = new User(2L, "user 2", "user2@email");
        itemRequestOne = new ItemRequest(1L, "item1", userOne, LocalDateTime.now());

        itemRequestTwo = new ItemRequest(2L, "description", userOne, LocalDateTime.now());


        item = new ItemRequestDto.Item(1L, "Test Item", "Item Description", true,
                userTwo.getId());

        itemForUser = new Item(1L, "name", "description", true, userOne, itemRequestTwo);

        itemRequestDto = new ItemRequestDto(1L, itemRequestOne.getDescription(),
                userOne.getId(), LocalDateTime.now(), List.of(item));
    }

    @Test
    void getUserRequests() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(Mockito.anyLong())).thenReturn(List.of(itemRequestOne));

        List<ItemRequestDto> itemRequestDtoTestList = itemRequestService.getUserRequests(userOne.getId());

        Assertions.assertEquals(itemRequestDtoTestList.size(), 1);
        assertEquals(itemRequestDtoTestList.get(0).getRequesterId(), itemRequestDto.getRequesterId());
        assertEquals(itemRequestDtoTestList.get(0).getId(), itemRequestDto.getId());
    }

    @Test
    void saveItem() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequestOne);

        ItemRequestDto itemRequestDtoTest = itemRequestService.saveItem(itemRequestDto, userOne.getId());

        assertNotNull(itemRequestDtoTest);
        assertEquals(itemRequestDtoTest.getRequesterId(), itemRequestDto.getRequesterId());
        assertEquals(itemRequestDtoTest.getId(), itemRequestDto.getId());
    }

    @Test
    void getOtherAllRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        itemRequestOne.setRequester(userTwo);
        itemRequestDto.setRequesterId(userTwo.getId());

        when(itemRequestRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(itemRequestOne)));
        List<ItemRequestDto> itemRequestDtoTestList = itemRequestService.getOtherAllRequests(userOne.getId(), pageable);

        Assertions.assertEquals(itemRequestDtoTestList.size(), 1);
        assertEquals(itemRequestDtoTestList.get(0).getRequesterId(), itemRequestDto.getRequesterId());
        assertEquals(itemRequestDtoTestList.get(0).getId(), itemRequestDto.getId());
    }

    @Test
    void getRequestById() {

        when(userRepository.existsById(userOne.getId())).thenReturn(true);
        when(userRepository.existsById(userTwo.getId())).thenReturn(false);
        when(itemRepository.existsById(userOne.getId())).thenReturn(true);
        when(itemRepository.existsById(userTwo.getId())).thenReturn(false);
        when(itemRequestRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemRequestOne);
        when(itemRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(List.of(itemForUser));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> itemRequestService
                .getRequestById(userOne.getId(), itemRequestTwo.getId()));
        assertEquals(thrown.getMessage(), "Такого запроса не существует");

        NotFoundException thrown1 = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(userTwo.getId(), itemRequestOne.getId()));
        assertEquals(thrown1.getMessage(), "Пользователь с таким id не найден");

        assertEquals(itemRequestService.getRequestById(userOne.getId(),
                itemRequestOne.getId()).getItems().size(), 1);
    }
}
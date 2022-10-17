package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebServiceClientTest(ItemService.class)
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;

    private ItemInfoDto itemInfoDtoOther;

    private ItemDto itemDtoOther;

    private CommentDto commentDto;
    private ItemRequest itemRequestOne;
    private User userOne;
    private User userTwo;

    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "user 1", "user1@email");
        userTwo = new User(2L, "user 2", "user2@email");
        itemRequestOne = new ItemRequest(1L, "item1", userOne, LocalDateTime.now());

        itemInfoDtoOther = new ItemInfoDto(
                1L,
                "item",
                "description",
                true,
                userOne.getId(),
                new ItemInfoDto.BookingDto(4L, 5L),
                new ItemInfoDto.BookingDto(6L, 7L),
                List.of(new CommentDto(8L, "comment",
                        LocalDateTime.of(2022, 1, 1, 1, 1), "author")),
                itemRequestOne.getRequester().getId()
        );

        itemDtoOther = new ItemDto(
                1L,
                "item",
                "description",
                true,
                userOne.getId(),
                itemRequestOne.getRequester().getId()
        );

        commentDto = new CommentDto(
                1L,
                "something",
                LocalDateTime.now(),
                "BorisTheBlade"
        );
    }

    @Test
    void getItems() {

        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setId(1L);
            item.setOwner(userTwo);
            return new PageImpl<>(List.of(item));
        });
        final List<ItemInfoDto> itemInfoDtoList = itemService.getItems(userTwo.getId(), PageRequest.of(0, 10));

        verify(bookingRepository, Mockito.times(1)).getBookingNext(1L);
        verify(bookingRepository, Mockito.times(1)).getBookingLast(1L);
        assertEquals(itemInfoDtoList.size(), 1);

    }

    @Test
    void addNewItem() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setId(1L);
            return item;
        });

        assertEquals(itemService.addNewItem(1L, itemDtoOther).getId(), 1L);
    }

    @Test
    void deleteItem() {
        Item deleteItem = new Item(3L, "item del", "del Item", true, userOne);
        when(itemRepository.getReferenceById(itemDtoOther.getId())).thenReturn(deleteItem);
        itemService.deleteItem(userOne.getId(), deleteItem.getId());

        verify(itemRepository, times(1)).deleteByIdAndOwnerId(deleteItem.getId(), userOne.getId());
    }

    @Test
    void updateItem() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(i -> {
            Item item = new Item();
            item.setOwner(userTwo);
            return item;
        });
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setId(1L);
            item.setOwner(userTwo);
            return item;
        });
        ItemDto itemDto = new ItemDto();

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 1L));
        assertEquals(thrown.getMessage(), "Итем с таким пользователем не найден");

        itemDto.setName("test");
        itemDto.setDescription("description");
        itemDto.setAvailable(false);
        assertNotNull(itemService.updateItem(2L, itemDto, 1L));

        Item item = new Item();
        item.setOwner(userTwo);
    }

    @Test
    void getItemById() {
        when(itemRepository.getReferenceById(1L)).thenAnswer(i -> {
            Item item = new Item();
            item.setOwner(userTwo);
            item.setId(1L);
            return item;
        });
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenAnswer(i -> {
            Comment comment = new Comment();
            comment.setUser(userTwo);
            return List.of(comment, comment);
        });
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(bookingRepository.getBookingLast(Mockito.anyLong())).thenReturn(new Booking());
        when(bookingRepository.getBookingNext(Mockito.anyLong())).thenReturn(new Booking());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> itemService.getItemById(2L,
                2L));
        assertEquals(thrown.getMessage(), "Такой предмет не найден");

        ItemInfoDto itemDto = itemService.getItemById(1L, 2L);

        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
        verify(userRepository, Mockito.times(2)).getReferenceById(2L);
    }

    @Test
    void getAvailableItems() {
        assertEquals(itemService.getAvailableItems("", null).size(), 0);
        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setName("item name");
            item.setDescription("test description");
            item.setAvailable(true);
            return new PageImpl<>(List.of(item));
        });
        assertEquals(itemService.getAvailableItems("something", PageRequest.of(0, 10)).size(), 0);
        assertEquals(itemService.getAvailableItems("test", PageRequest.of(0, 10)).size(), 1);
    }

    @Test
    void addComment() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(new Comment());
        when(bookingRepository.isBookingExists(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(BookingState.class)))
                .thenReturn(true);
        ValidationException thrown = assertThrows(ValidationException.class, () ->
                itemService.addComment(1L, 1L, new CommentDto(null, "", null, null))
        );
        assertEquals(thrown.getMessage(), "Ошибка добавления комментария");
        assertNotNull(itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void saveInvalidItem() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        ItemDto itemDto = new ItemDto();

        NotFoundException exceptionUser = assertThrows(NotFoundException.class, () ->
                itemService.addNewItem(2L, itemDto)
        );
        assertEquals(exceptionUser.getMessage(), "Пользователи с таким id не найдены. Ошибка добавления предмета");

        ValidationException exceptionAvailable = assertThrows(ValidationException.class, () ->
                itemService.addNewItem(1L, itemDto)
        );
        assertEquals(exceptionAvailable.getMessage(), "Ошибка, укажите доступность item");
        itemDto.setAvailable(true);

        ValidationException exceptionName = assertThrows(ValidationException.class, () ->
                itemService.addNewItem(1L, itemDto)
        );
        assertEquals(exceptionName.getMessage(), "Ошибка, укажите название item");
        itemDto.setName("name");

        ValidationException exceptionDesc = assertThrows(ValidationException.class, () ->
                itemService.addNewItem(1L, itemDto)
        );
        assertEquals(exceptionDesc.getMessage(), "Ошибка, укажите описание item");
    }
}
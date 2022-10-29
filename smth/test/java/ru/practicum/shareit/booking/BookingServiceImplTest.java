package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebServiceClientTest(BookingService.class)
class BookingServiceImplTest {

    @Autowired
    BookingService bookingService;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;

    private BookingDto bookingDto;

    private ItemRequest itemRequestOne;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;

    private Booking booking;
    private Booking bookingTwo;

    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "user 1", "user1@email");
        userTwo = new User(2L, "user 2", "user2@email");

        itemOne = new Item(1L, "ItemOne", "ItemOne desc", true, userOne);
        itemTwo = new Item(2L, "ItemTwo", "ItemTwo desc", true, userTwo);

        itemRequestOne = new ItemRequest(1L, "item1", userOne, LocalDateTime.now());

        bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2022, 11, 12, 11, 11),
                LocalDateTime.of(2022, 12, 12, 11, 11),
                BookingState.ALL,
                new BookingDto.UserNewDto(1L, "user", "user@mail.com"),
                new BookingDto.ItemNewDto(1L, "Item", "Item Description", true, 1L,
                        new BookingShortDto(), new BookingShortDto(), null)
        );

        booking = new Booking(
                1L,
                1L,
                LocalDateTime.of(2022, 11, 12, 11, 11),
                LocalDateTime.of(2022, 12, 12, 11, 11),
                BookingState.ALL,
                1L
        );

        bookingTwo = new Booking(
                2L,
                2L,
                LocalDateTime.of(2022, 11, 12, 11, 11),
                LocalDateTime.of(2022, 12, 12, 11, 11),
                BookingState.ALL,
                1L
        );
    }

    @Test
    void save() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(a -> {
            return itemOne;
        });
        when(bookingRepository.save(Mockito.any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDto, userOne.getId()));
        assertEquals(thrown.getMessage(), "владелец предмета не может его бронировать");

        assertEquals(bookingService.save(bookingDto, userTwo.getId()).getStatus(), BookingState.WAITING);
    }

    @Test
    void saveInvalidEntity() {
        when(userRepository.existsById(userOne.getId())).thenReturn(true);
        when(userRepository.existsById(userTwo.getId())).thenReturn(false);
        when(itemRepository.existsById(userOne.getId())).thenReturn(false);
        when(itemRepository.existsById(userTwo.getId())).thenReturn(true);
        when(itemRepository.getReferenceById(itemTwo.getId())).thenAnswer(i -> {
            itemOne.setAvailable(false);
            return itemOne;
        });

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDto, userTwo.getId()));
        assertEquals(exception.getMessage(), "такого пользователя не существует");

        bookingDto.setItemId(22L);
        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDto, userOne.getId()));
        assertEquals(thrown.getMessage(), "предмет не найден");

        bookingDto.setItemId(itemTwo.getId());
        ValidationException thrown1 = assertThrows(ValidationException.class,
                () -> bookingService.save(bookingDto, userOne.getId()));
        assertEquals(thrown1.getMessage(), "предмет нельзя забронировать");
    }

    @Test
    void approveBooking() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(bookingRepository.getReferenceById(Mockito.anyLong())).thenAnswer(i -> {
            Long id = (Long) i.getArguments()[0];
            booking.setItemId(id);
            booking.setBookerId(1L);
            booking.setStatus(id.equals(2L) ? BookingState.APPROVED : BookingState.WAITING);
            return booking;
        });
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(i -> {

            Long id = (Long) i.getArguments()[0];
            Item item = new Item();
            item.setId(1L);

            User user = new User();
            user.setId(id);

            item.setOwner(user);
            return item;
        });

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(userOne.getId(), bookingTwo.getId(), true));
        assertEquals(thrown.getMessage(), "бронирование уже подтверждено");

        NotFoundException thrown2 = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(userOne.getId(), 3L, true));
        assertEquals(thrown2.getMessage(), "не удалось подтвердить бронирование");

        assertEquals(bookingService.approveBooking(userOne.getId(), booking.getId(), true).getStatus(),
                BookingState.APPROVED);
        assertEquals(bookingService.approveBooking(userOne.getId(), booking.getId(), false).getStatus(),
                BookingState.REJECTED);
    }

    @Test
    void getBooking() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsById(2L)).thenReturn(false);
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(a -> {
            return itemOne;
        });
        when(bookingRepository.getReferenceById(Mockito.anyLong())).thenAnswer(a -> {
            return booking;
        });
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(userOne.getId(), bookingTwo.getId())
        );
        assertEquals(thrown.getMessage(), "не удалось найти бронирование");

        BookingDto bookingDto = bookingService.getBooking(userOne.getId(), booking.getId());
        BookingDto.ItemNewDto item = bookingDto.getItem();
        assertEquals(item.getOwner(), 1);
        assertNotNull(bookingDto.getBooker());
    }

    @Test
    void getAllBookings() {

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userOne);
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemOne);

        when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findBookerAllByPast(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllByCurrent(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllByFuture(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllByStatus(1L, BookingState.WAITING)).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllByStatus(1L, BookingState.REJECTED)).thenReturn(List.of());

        assertEquals(bookingService.getAllBookings(1L, BookingState.ALL.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingState.PAST.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingState.CURRENT.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingState.FUTURE.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingState.WAITING.name(),
                PageRequest.of(0, 10)).size(), 1);

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookings(1L, BookingState.REJECTED.name(),
                        PageRequest.of(0, 10)));
        assertEquals(thrown.getMessage(), "не удалось найти список бронирований");
    }

    @Test
    void getOwnerAllBookings() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userOne);
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemOne);
        when(bookingRepository.findAllByItemIdInOrderByStartDesc(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findItemsInThePast(Mockito.anyList(),
                Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findItemsInTheCurrent(Mockito.anyList(),
                Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findItemsInTheFuture(Mockito.anyList(),
                Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findItemsByStatus(List.of(),
                BookingState.APPROVED)).thenReturn(List.of(booking));
        when(bookingRepository.findItemsByStatus(List.of(),
                BookingState.REJECTED)).thenReturn(List.of());

        assertEquals(bookingService.getOwnerAllBookings(1L, BookingState.ALL.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingState.PAST.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingState.CURRENT.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingState.FUTURE.name(),
                PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingState.APPROVED.name(),
                PageRequest.of(0, 10)).size(), 1);

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                bookingService.getOwnerAllBookings(1L, BookingState.REJECTED.name(),
                        PageRequest.of(0, 10)));
        assertEquals(thrown.getMessage(), "не удалось найти список бронирований");
    }

    @Test
    void getLastBooking() {
        booking.setStatus(BookingState.APPROVED);
        booking.setBookerId(4L);

        when(bookingRepository.getBookingLast(Mockito.anyLong())).thenReturn(booking);
        BookingShortDto bookingShortDto = bookingService.getLastBooking(1L);
        assertEquals(bookingShortDto.getId(), 1L);
        assertEquals(bookingShortDto.getBookerId(), 4L);
    }

    @Test
    void getNextBooking() {
        booking.setStatus(BookingState.APPROVED);
        booking.setBookerId(4L);

        when(bookingRepository.getBookingNext(Mockito.anyLong())).thenReturn(booking);
        BookingShortDto bookingShortDto = bookingService.getNextBooking(1L);
        assertEquals(bookingShortDto.getId(), 1L);
        assertEquals(bookingShortDto.getBookerId(), 4L);
    }
}
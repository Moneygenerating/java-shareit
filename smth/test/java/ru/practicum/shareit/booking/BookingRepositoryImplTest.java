package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryImplTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TestEntityManager em;

    @Test
    void checkBooking() {

        em.persist(new User(null, "name", "name@email.ru"));
        em.persist(new Item(null, "name", "description",
                true, new User("name2","dsd@mail.com"), null));
        em.persist(new Booking(null, 1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                BookingState.APPROVED, 1L));
        em.persist(new Booking(null, 1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingState.APPROVED, 1L));

        assertNotNull(bookingRepository.getBookingLast(1L));
        assertNull(bookingRepository.getBookingLast(2L));
        assertNotNull(bookingRepository.getBookingNext(1L));

        assertTrue(bookingRepository.isBookingExists(1L, 1L, BookingState.APPROVED));
        assertFalse(bookingRepository.isBookingExists(1L, 1L, BookingState.REJECTED));
    }
}
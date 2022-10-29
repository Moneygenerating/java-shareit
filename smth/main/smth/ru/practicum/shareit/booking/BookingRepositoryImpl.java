package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;

public class BookingRepositoryImpl implements BookingRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Booking getBookingLast(Long itemId) {
        Query q = entityManager
                .createQuery("SELECT b FROM Booking b WHERE b.itemId" +
                        " IN ?1 AND b.status = ?2 AND b.end < ?3 ORDER BY b.start DESC", Booking.class);
        q.setParameter(1, itemId);
        q.setParameter(2, BookingState.APPROVED);
        q.setParameter(3, LocalDateTime.now());
        try {
            return (Booking) q.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Booking getBookingNext(Long itemId) {
        Query q = entityManager
                .createQuery("SELECT b FROM Booking b WHERE b.itemId = ?1" +
                        " AND b.status = ?2 AND b.start > ?3 ORDER BY b.start DESC", Booking.class);
        q.setParameter(1, itemId);
        q.setParameter(2, BookingState.APPROVED);
        q.setParameter(3, LocalDateTime.now());
        try {
            return (Booking) q.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean isBookingExists(Long userId, Long bookerId, BookingState state) {
        Query q = entityManager
                .createQuery("SELECT b FROM Booking b WHERE b.bookerId = ?1" +
                        " AND b.itemId = ?2 AND b.status = ?3 AND b.start < ?4 ORDER BY b.start DESC", Booking.class);
        q.setParameter(1, userId);
        q.setParameter(2, bookerId);
        q.setParameter(3, state);
        q.setParameter(4, LocalDateTime.now());
        return q.getResultList().size() > 0;
    }
}

package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND ?2 BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findBookerAllByCurrent(Long bookerId, LocalDateTime date);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findBookerAllByPast(Long bookerId, LocalDateTime date);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findBookerAllByFuture(Long bookerId, LocalDateTime date);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findBookerAllByStatus(Long bookerId, BookingState status);

    Page<Booking> findAllByItemIdInOrderByStartDesc(List<Long> itemIds, Pageable pageable);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findItemsInThePast(List<Long> itemIds, LocalDateTime dt);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findItemsInTheFuture(List<Long> itemIds, LocalDateTime dt);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND ?2 BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findItemsInTheCurrent(List<Long> itemIds, LocalDateTime dt);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findItemsByStatus(List<Long> itemIds, BookingState status);
}

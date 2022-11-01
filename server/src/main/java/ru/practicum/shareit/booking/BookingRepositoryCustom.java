package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

public interface BookingRepositoryCustom {

    Booking getBookingLast(Long itemId);

    Booking getBookingNext(Long itemId);

    Boolean isBookingExists(Long userId, Long bookerId, BookingState state);
}

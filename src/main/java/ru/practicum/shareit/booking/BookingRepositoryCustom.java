package ru.practicum.shareit.booking;

public interface BookingRepositoryCustom {

    Booking getBookingLast(Long itemId);

    Booking getBookingNext(Long itemId);

    Boolean isBookingExists(Long userId, Long bookerId, BookingState state);
}

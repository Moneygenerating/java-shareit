package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.service.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = parseStatusOrThrownException(stateParam);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Validated({Create.class}) BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        log.info("getOwnerAllBookings");
        BookingState stateParam = parseStatusOrThrownException(state);
        if (stateParam == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingClient.getOwnerBookings(userId, stateParam, from, size);
    }

    private BookingState parseStatusOrThrownException(String status) {
        try {
            return BookingState.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + status);
        }
    }
}

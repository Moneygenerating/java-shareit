package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.service.Create;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    @Autowired
    BookingService bookingService;

    @PostMapping
    public BookingDto postBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Validated({Create.class}) BookingDto bookingDto) {
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "50") Integer size) {

        log.info("getAllBookings");
        BookingState stateParam = BookingState.from(state);
        if (stateParam == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingService.getAllBookings(userId, state, PageRequest.of(from / size, size, Sort.Direction.DESC, "start"));
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "50") Integer size) {

        log.info("getOwnerAllBookings");
        BookingState stateParam = BookingState.from(state);
        if (stateParam == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingService.getOwnerAllBookings(userId, state, PageRequest.of(from / size, size));
    }
}

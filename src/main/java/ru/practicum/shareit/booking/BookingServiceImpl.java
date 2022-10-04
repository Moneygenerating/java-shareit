package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto save(BookingDto bookingDto, Long userId) {
        if (checkUserExist(userId) && isBookingFieldsExists(bookingDto)) {
            Booking booking = BookingMapper.dtoToBooking(bookingDto);
            Item item = itemRepository.getReferenceById(booking.getItemId());
            if (item.getOwner().getId().equals(userId)) {
                throw new NotFoundException("владелец предмета не может его бронировать");
            }
            booking.setBookerId(userId);
            booking.setStatus(BookingState.WAITING);
            return BookingMapper.bookingToDto(bookingRepository.save(booking));
        }
        throw new ValidationException("не удалось сохранить бронирование");
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItemId());
        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new ValidationException("бронирование уже подтверждено");
        }
        if (checkUserExist(userId) && Objects.equals(item.getOwner().getId(), userId)) {
            booking.setStatus(isApproved ? BookingState.APPROVED : BookingState.REJECTED);
            bookingRepository.save(booking);
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setItem(BookingMapper.itemToBookingNewDto(item));
            bookingDto.setBooker(BookingMapper.userToBookingNewDto(userRepository
                    .getReferenceById(booking.getBookerId())));
            return bookingDto;
        }
        throw new NotFoundException("не удалось подтвердить бронирование");
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        if (bookingRepository.existsById(bookingId)) {
            Booking booking = bookingRepository.getReferenceById(bookingId);
            Item item = itemRepository.getReferenceById(booking.getItemId());
            if (checkUserExist(userId) && (booking.getBookerId().equals(userId)
                    || item.getOwner().getId().equals(userId))) {
                BookingDto bookingDto = BookingMapper.bookingToDto(booking);
                bookingDto.setItem(BookingMapper.itemToBookingNewDto(item));
                bookingDto.setBooker(BookingMapper.userToBookingNewDto(userRepository
                        .getReferenceById(booking.getBookerId())));
                return bookingDto;
            }
        }
        throw new NotFoundException("не удалось найти бронирование");
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, String status) {
        List<Booking> bookings;
        BookingState bookingStatus = parseStatus(status);
        LocalDateTime dt = LocalDateTime.now();
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findBookerAllByPast(userId, dt);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookerAllByCurrent(userId, dt);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookerAllByFuture(userId, dt);
                break;
            default:
                bookings = bookingRepository.findBookerAllByStatus(userId, bookingStatus);
        }
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(BookingMapper.userToBookingNewDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(BookingMapper.itemToBookingNewDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtos.size() > 0) {
            return bookingDtos;
        }
        throw new NotFoundException("не удалось найти список бронирований");
    }

    @Override
    public List<BookingDto> getOwnerAllBookings(Long userId, String status) {
        List<Long> idsList = itemRepository.findAllByOwner(userId).stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings;

        LocalDateTime dt = LocalDateTime.now();
        BookingState bookingStatus = parseStatus(status);
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(idsList);
                break;
            case PAST:
                bookings = bookingRepository.findItemsInThePast(idsList, dt);
                break;
            case CURRENT:
                bookings = bookingRepository.findItemsInTheCurrent(idsList, dt);
                break;
            case FUTURE:
                bookings = bookingRepository.findItemsInTheFuture(idsList, dt);
                break;
            default:
                bookings = bookingRepository.findItemsByStatus(idsList, bookingStatus);
        }
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(BookingMapper.userToBookingNewDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(BookingMapper.itemToBookingNewDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtos.size() > 0) {
            return bookingDtos;
        }

        throw new NotFoundException("не удалось найти список бронирований");
    }

    @Override
    public BookingShortDto getLastBooking(Long itemId) {
        return BookingMapper.bookingToShortDto(bookingRepository.getBookingLast(itemId));
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {
        return BookingMapper.bookingToShortDto(bookingRepository.getBookingNext(itemId));
    }

    private BookingState parseStatus(String status) {
        try {
            return BookingState.valueOf(status);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + status);
        }
    }

    private Boolean checkUserExist(long userId) {
        if (userRepository.existsById(userId)) {
            return true;
        }
        throw new NotFoundException("такого пользователя не существует");
    }

    private Boolean isBookingFieldsExists(BookingDto bookingDto) {
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new NotFoundException("предмет не найден");
        }
        if (!itemRepository.getReferenceById(bookingDto.getItemId()).getAvailable()) {
            throw new ValidationException("предмет нельзя забронировать");
        }
        return true;
    }
}

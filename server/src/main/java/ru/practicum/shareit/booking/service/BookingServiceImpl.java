package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = {Exception.class})
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

            BookingDto bookingDtoResult = BookingMapper.bookingToDto(bookingRepository.save(booking));
            bookingDtoResult.setItem(BookingMapper.itemToBookingNewDto(itemRepository.getReferenceById(booking.getItemId())));

            UserDto userDto = UserMapper.toUserDto(userRepository.getReferenceById(booking.getBookerId()));
            bookingDtoResult.setBooker(new BookingDto.UserNewDto(userDto.getId(), userDto.getName(),userDto.getEmail()));
            return bookingDtoResult;
        }
        throw new ValidationException("не удалось сохранить бронирование");
    }

    @Transactional(rollbackFor = {Exception.class})
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
    public List<BookingDto> getAllBookings(Long userId, String status, Pageable pageable) {
        List<Booking> bookings;
        BookingState bookingStatus = parseStatus(status);
        LocalDateTime dt = LocalDateTime.now();
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageable)
                        .stream().collect(Collectors.toList());
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
        return getBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getOwnerAllBookings(Long userId, String status, Pageable pageable) {
        List<Long> idsList = itemRepository
                .findAllByOwner(userRepository.getReferenceById(userId))
                .stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings;

        LocalDateTime dt = LocalDateTime.now();
        BookingState bookingStatus = parseStatus(status);
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(idsList, pageable)
                        .stream().collect(Collectors.toList());
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
        return getBookingDtoList(bookings);
    }

    private List<BookingDto> getBookingDtoList(List<Booking> bookings) {
        List<BookingDto> bookingDtoList = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(BookingMapper.userToBookingNewDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(BookingMapper.itemToBookingNewDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtoList.size() > 0) {
            return bookingDtoList;
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

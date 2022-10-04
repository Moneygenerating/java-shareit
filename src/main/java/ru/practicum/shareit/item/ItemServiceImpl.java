package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemInfoDto> getItems(Long userId) {

        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemInfoDto)
                .peek(itemInfoDto -> {
                    if (itemInfoDto.getOwner().equals(userId)) {
                        setLastAndNextBooking(itemInfoDto);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователи с таким id не найдены. Ошибка добавления предмета");
        }

        if (isExistCheckFieldsOfItem(itemDto)) {
            User user = userRepository.getReferenceById(userId);
            Item item = ItemMapper.toItem(itemDto, user);
            return ItemMapper.toItemDto(itemRepository.save(item));
        }
        return null;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, Long itemId) {

        Item itemForUpdate = itemRepository.getReferenceById(itemId);

        if (itemForUpdate.getOwner().getId() != userId) {
            throw new NotFoundException("Итем с таким пользователем не найден");
        } else {
            if (itemDto.getName() != null) {
                itemForUpdate.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemForUpdate.setDescription(itemDto.getDescription());
            }

            if (itemDto.getAvailable() != null) {
                itemForUpdate.setAvailable(itemDto.getAvailable());
            }

            return ItemMapper.toItemDto(itemRepository.save(itemForUpdate));
        }
    }

    @Override
    public ItemInfoDto getItemById(Long itemId, long userId) {
        try {
            ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(itemRepository.getReferenceById(itemId));
            itemInfoDto.setComments(commentRepository.findAllByItemId(itemId)
                    .stream().map(comment -> {
                        User user = userRepository.getReferenceById(comment.getUser().getId());
                        return CommentMapper.commentToDto(comment, user.getName());
                    }).collect(Collectors.toList()));

            if (itemInfoDto.getOwner().equals(userId)) {
                setLastAndNextBooking(itemInfoDto);
            }
            return itemInfoDto;
        } catch (Exception e) {
            throw new NotFoundException("Такой предмет не найден");
        }
    }

    @Override
    public List<ItemDto> getAvailableItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findAll()
                    .stream()
                    .filter(item ->
                            item.getAvailable()
                                    && (item.getName() + " " + item.getDescription()).toLowerCase().contains(text)
                    )
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (!commentDto.getText().isBlank() &&
                bookingRepository.isBookingExists(userId, itemId, BookingState.APPROVED)) {
            User user = userRepository.getReferenceById(userId);
            Item item = itemRepository.getReferenceById(itemId);
            Comment comment = CommentMapper.dtoToComment(commentDto, item, user, LocalDateTime.now());
            return CommentMapper.commentToDto(commentRepository.save(comment), user.getName());
        }
        throw new ValidationException("Ошибка добавления комментария");
    }

    private boolean isExistCheckFieldsOfItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка, укажите доступность item");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Ошибка, укажите название item");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Ошибка, укажите описание item");
        }
        return true;
    }

    private void setLastAndNextBooking(ItemInfoDto itemInfoDto) {
        try {
            //setLastBooking
            Booking lastBooking = bookingRepository
                    .getBookingLast(itemInfoDto.getId());

            if (lastBooking != null) {
                ItemInfoDto.BookingDto lastBookingDto = new ItemInfoDto.BookingDto();
                lastBookingDto.setId(lastBooking.getId());
                lastBookingDto.setBookerId(lastBooking.getBookerId());
                itemInfoDto.setLastBooking(lastBookingDto);
            }
            //setNextBooking
            Booking nextBooking = bookingRepository
                    .getBookingNext(itemInfoDto.getId());

            if (nextBooking != null) {
                ItemInfoDto.BookingDto nextBookingDto = new ItemInfoDto.BookingDto();
                nextBookingDto.setId(nextBooking.getId());
                nextBookingDto.setBookerId(nextBooking.getBookerId());
                itemInfoDto.setNextBooking(nextBookingDto);
            }
        } catch (Exception e) {
            return;
        }
    }
}


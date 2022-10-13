package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> getUserRequests(Long requesterId) {
        //проверим есть ли такой юзер
        checkUserEntityById(requesterId);

        return itemRequestRepository.findAllByRequesterId(requesterId)
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequestDto.getId()));
                    return itemRequestDto;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, ValidationException.class})
    public ItemRequestDto saveItem(ItemRequestDto itemRequestDto, Long userId) {
        //проверим есть ли такой юзер
        checkUserEntityById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, getUserById(userId));
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOtherAllRequests(Long userId, Pageable pageable) {

        return itemRequestRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequestDto.getId()));
                    return itemRequestDto;
                }).filter(itemRequestDto -> !Objects.equals(itemRequestDto.getRequesterId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        checkUserEntityById(userId);
        if (itemRepository.existsById(requestId)) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(
                    itemRequestRepository.getReferenceById(requestId));
            itemRequestDto.setItems(getItems(itemRequestDto.getRequesterId()));
            return itemRequestDto;
        }

        throw new NotFoundException("Такого запроса не существует");
    }

    private List<ItemRequestDto.Item> getItems(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return items.stream()
                .map(item -> new ItemRequestDto.Item(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        item.getRequest().getId()
                )).collect(Collectors.toList());
    }

    private void checkUserEntityById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    private User getUserById(Long id) {
        return userRepository.getReferenceById(id);
    }

}

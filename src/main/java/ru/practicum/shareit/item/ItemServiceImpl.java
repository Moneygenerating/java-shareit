package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;


    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {

        return itemDao.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        boolean matchUer = userRepository.findAll()
                .stream()
                .anyMatch(user -> Objects.equals(user.getId(), userId));

        if (!matchUer) {
            throw new NotFoundException("Такого предмета нет");
        }
        User user = userRepository.getReferenceById(userId);


        Item item = ItemMapper.toItem(itemDto, user);

        return ItemMapper.toItemDto(itemDao.create(item));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemDao.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, Long itemId) {


        //валидация неверно переданного пользователя
        Item itemForValidate = itemDao.getItemById(itemId);

        if (itemForValidate.getOwner().getId() != userId) {
            throw new NotFoundException("Итем с таким пользователем не найден");
        }

        Item item = ItemMapper.toItem(itemDto, userRepository.getReferenceById(userId));
        return ItemMapper.toItemDto(itemDao.updateItem(itemId, item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemDao.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAvailableItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            text = text.toLowerCase();

            return itemDao.getAvailableItems(text)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}


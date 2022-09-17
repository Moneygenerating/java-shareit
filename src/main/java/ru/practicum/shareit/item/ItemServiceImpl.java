package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;


    private final UserDao userDao;

    @Override
    public List<ItemDto> getItems(Long userId) {

        return itemDao.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = userDao.getUserById(userId);

        if (user.getId() == null) {
            throw new NotFoundException("Такого предмета нет");
        }

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

        Item item = ItemMapper.toItem(itemDto, userDao.getUserById(userId));
        return ItemMapper.toItemDto(itemDao.updateItem(itemId, item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemDao.getItemById(itemId));
    }

    @Override
    public ItemDto getAvailableItem(long userId, String text) {
        text = text.toLowerCase();

        ItemDto itemDto = ItemMapper.toItemDto(itemDao.getAvailableItem(userId, text));

        if (!itemDto.getAvailable()) {
            throw new ValidationException("Предмет не доступен");
        } else {
            return itemDto;
        }
    }
}

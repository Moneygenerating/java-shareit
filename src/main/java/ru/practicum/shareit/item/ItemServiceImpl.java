package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    @Override
    public List<ItemDto> getItems(Long userId) {

        return itemDao.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemDao.save(userId, item));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemDao.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, Long itemId) {
        itemDto.setId(itemId);
        Item item = ItemMapper.toItem(itemDto, userId);
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

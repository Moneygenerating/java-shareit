package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}

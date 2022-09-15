package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);
    ItemDto addNewItem(Long userId, ItemDto item);
    void deleteItem(Long userId, Long itemId);
    ItemDto updateItem(long userId, ItemDto itemDto, Long itemId);

    ItemDto getItemById(Long itemId);

    ItemDto getAvailableItem(long userId, String text);

}

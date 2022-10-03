package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    ItemDto updateItem(long userId, ItemDto itemDto, Long itemId);

    ItemInfoDto getItemById(Long itemId, long userId);

    List<ItemDto> getAvailableItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

}

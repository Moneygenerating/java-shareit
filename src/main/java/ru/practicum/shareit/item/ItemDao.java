package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    List<Item> findByUserId(long userId);

    Item create(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item updateItem(Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> getAvailableItems(String text);

}

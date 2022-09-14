package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    protected List<Item> items;
    private long generatorId;

    public ItemDaoImpl() {
        items = new ArrayList<>();
        generatorId = 0;
    }

    @Override
    public List<Item> findByUserId(long userId) {

        return items
                .stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item save(long userId, Item item) {
        generatorId++;
        item.setOwner(userId);
        item.setId(generatorId);
        items.add(item);
        return items.stream()
                .filter(item1 -> Objects.equals(item1.getId(), generatorId))
                .collect(Collectors.toList()).get(0);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        Item itemForDelete = items
                .stream()
                .filter(item -> item.getOwner() == userId && item.getId() == itemId)
                .collect(Collectors.toList()).get(0);
        items.remove(itemForDelete);
    }
}

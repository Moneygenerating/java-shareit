package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    protected Set<Item> items;
    private long generatorId;

    public ItemDaoImpl() {
        items = new HashSet<>();
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

    @Override
    public Item updateItem(Long itemId, Item item) {
        //вытаскиваем старый итем
        Item itemForUpdate = items
                .stream()
                .filter(item2 -> item2.getId() == itemId)
                .collect(Collectors.toList()).get(0);
        //удаляем его
        items.remove(itemForUpdate);
        //спорно но можно
        if (item.getId() != null) {
            itemForUpdate.setId(item.getId());
        }
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        items.add(itemForUpdate);
        return itemForUpdate;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items
                .stream()
                .filter(item2 -> item2.getId() == itemId)
                .collect(Collectors.toList()).get(0);
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam  String text) {
        return itemService.getAvailableItems(userId, text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated({Update.class}) @RequestBody ItemDto itemDto,
                       @PathVariable Long itemId) {
        return itemService.updateItem(userId,itemDto,itemId);
    }

}

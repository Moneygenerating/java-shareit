package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<ItemInfoDto> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestParam(value = "from", required = false,
                                         defaultValue = "0") Integer from,
                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Запрос item get item");
        return itemService.getItems(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getByItemId(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        log.info("Запрос item get getByItemId");
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItem(@RequestParam String text,
                                          @RequestParam(value = "from", required = false,
                                                  defaultValue = "0") Integer from,
                                          @RequestParam(value = "size", required = false,
                                                  defaultValue = "10") Integer size) {
        log.info("Запрос item get /search");
        return itemService.getAvailableItems(text.toLowerCase(), PageRequest.of(from / size, size));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto item) {
        log.info("Запрос item post");
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Запрос item delete");
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @RequestBody ItemDto itemDto,
                       @PathVariable Long itemId) {
        log.info("Запрос item update");
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}

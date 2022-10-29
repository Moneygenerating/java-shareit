package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(value = "from", required = false,
                                              defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Запрос item get item");
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        log.info("Запрос item get getByItemId");
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam String text,
                                                   @RequestParam(value = "from", required = false,
                                                           defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(value = "size", required = false,
                                                           defaultValue = "10") Integer size) {
        log.info("Запрос item get /search");
        if (text.isBlank()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }

        return itemClient.findItem(userId, text.toLowerCase(), from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Validated({Create.class}) @RequestBody ItemDto item) {
        log.info("Запрос item post");
        return itemClient.saveItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Запрос item delete");
        itemClient.delete(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated({Update.class}) @RequestBody ItemDto itemDto,
                       @PathVariable Long itemId) {
        log.info("Запрос item update");
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}

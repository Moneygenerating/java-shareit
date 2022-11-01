package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    //получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getUserRequests");
        return itemRequestClient.getAllUserRequests(userId);
    }

    //добавить новый запрос вещи
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("createItemRequest");
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Не указано описание");
        }
        return itemRequestClient.save(itemRequestDto, userId);
    }

    //получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public ResponseEntity<Object> getOtherAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        log.info("getOtherAllRequests");
        return itemRequestClient.getOtherUserRequests(userId, from, size);
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
    // что и в эндпоинте
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("requestId") Long requestId) {

        log.info("getRequestById");
        return itemRequestClient.getById(userId, requestId);
    }
}

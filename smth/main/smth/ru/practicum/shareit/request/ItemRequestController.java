package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class ItemRequestController {

    @Autowired
    private ItemRequestService itemRequestService;

    //получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getUserRequests");
        return itemRequestService.getUserRequests(userId);
    }

    //добавить новый запрос вещи
    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            //@Valid
                                             @RequestBody ItemRequestDto itemRequestDto) {
        log.info("createItemRequest");

        return itemRequestService.saveItem(itemRequestDto, userId);
    }

    //получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public List<ItemRequestDto> getOtherAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            //@PositiveOrZero int from
            @RequestParam(value = "from", required = false, defaultValue = "0")  int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        log.info("getOtherAllRequests");
        return itemRequestService.getOtherAllRequests(userId,
                PageRequest.of(from / size, size, Sort.Direction.DESC, "created"));
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
    // что и в эндпоинте
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("requestId") Long requestId) {

        log.info("getRequestById");
        return itemRequestService.getRequestById(userId, requestId);
    }
}

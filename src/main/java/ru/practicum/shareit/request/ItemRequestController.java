package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    @Autowired
    private ItemRequestService itemRequestService;

    //получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("getAllItemRequests");
        return itemRequestService.getUserRequests(userId);
    }

    //добавить новый запрос вещи
    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("createItemRequest");

        return itemRequestService.saveItem(itemRequestDto, userId);
    }

    //получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public List<ItemRequestDto> getOtherAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size) {

        log.info("getOtherAllRequests");
        return itemRequestService.getOtherAllRequests(userId,
                PageRequest.of(from / size, size, Sort.Direction.DESC, "created"));
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
    // что и в эндпоинте
    @GetMapping
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam("requestId") Long requestId) {

        log.info("getRequestById");
        return itemRequestService.getRequestById(userId, requestId);
    }
}

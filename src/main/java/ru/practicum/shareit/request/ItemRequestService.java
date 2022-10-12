package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getUserRequests(Long userId);

    ItemRequestDto saveItem(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getOtherAllRequests(Long userId, Pageable pageable);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}


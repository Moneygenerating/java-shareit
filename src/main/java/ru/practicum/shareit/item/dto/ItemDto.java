package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {
    //уникальный идентификатор вещи
    private Long id;
    //краткое название
    private String name;
    //развёрнутое описание
    private String description;
    //статус о том, доступна или нет вещь для аренды
    private boolean available;
    //владелец вещи
    private User owner;
    //ссылка на соответствующий запрос
    private ItemRequest request;
}

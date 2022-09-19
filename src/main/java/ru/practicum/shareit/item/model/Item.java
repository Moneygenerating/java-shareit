package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    //уникальный идентификатор вещи
    private Long id;
    //краткое название
    private String name;
    //развёрнутое описание
    private String description;
    //статус о том, доступна или нет вещь для аренды
    private Boolean available;
    //владелец вещи
    private User owner;
    //ссылка на соответствующий запрос
    private ItemRequest request;

    public Item(String name, String description, Boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}

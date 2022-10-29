package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto {
    //уникальный идентификатор вещи

    private Long id;
    //краткое название
    private String name;
    //развёрнутое описание
    private String description;
    //статус о том, доступна или нет вещь для аренды
    private Boolean available;
    //владелец вещи
    private Long ownerId;
    //ссылка на соответствующий запрос
    private Long requestId;
}

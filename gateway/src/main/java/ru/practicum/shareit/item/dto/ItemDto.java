package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.service.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto {
    //уникальный идентификатор вещи
    private Long id;
    //краткое название
    @NotBlank(groups = {Create.class})
    private String name;
    //развёрнутое описание
    @NotBlank(groups = {Create.class})
    private String description;
    //статус о том, доступна или нет вещь для аренды
    @NotNull(groups = {Create.class})
    private Boolean available;
    //владелец вещи
    private Long ownerId;
    //ссылка на соответствующий запрос
    private Long requestId;
}

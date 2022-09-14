package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ItemDto {
    //уникальный идентификатор вещи
    @NotNull(groups = {Update.class})
    private Long id;
    //краткое название
    @NotBlank(groups = {Create.class, Update.class})
    private String name;
    //развёрнутое описание
    @NotBlank(groups = {Create.class, Update.class})
    private String description;
    //статус о том, доступна или нет вещь для аренды
    @NotNull(groups = {Create.class, Update.class})
    private Boolean available;
    //владелец вещи
    private Long owner;
    //ссылка на соответствующий запрос
    private ItemRequest request;

    public ItemDto(String name, String description, boolean available, Long owner){
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

}

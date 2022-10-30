package ru.practicum.shareit.itemRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    //уникальный идентификатор запроса
    private Long id;
    //текст запроса, содержащий описание требуемой вещи
    //@NotEmpty(groups = {Create.class, Update.class})
    private String description;
    //пользователь, создавший запрос
    private Long requesterId;
    //дата и время создания запроса
    private LocalDateTime created;
    //список items
    private List<Item> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}

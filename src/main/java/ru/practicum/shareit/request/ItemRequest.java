package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    //уникальный идентификатор запроса
    private Long id;
    //текст запроса, содержащий описание требуемой вещи
    private String description;
    //пользователь, создавший запрос
    private User requestor;
    //дата и время создания запроса
    private LocalDate created;
}

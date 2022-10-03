package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.service.Create;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    //уникальный идентификатор пользователя
    private Long id;
    //имя или логин пользователя
    private String name;
    //email TODO
    @NotNull(groups = {Create.class})
    private String email;

}
package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.service.Create;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    //уникальный идентификатор пользователя
    private Long id;
    //имя или логин пользователя
    private String name;
    //email
    @NotNull(groups = {Create.class})
    //@UniqueElements(groups = {Update.class})
    private String email;

}
package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    //уникальный идентификатор пользователя
    @NotNull(groups = {Update.class})
    private Long id;
    //имя или логин пользователя
    @NotNull(groups = {Update.class})
    private String name;
    //email
    @NotNull(groups = {Create.class, Update.class})
    private String email;
    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
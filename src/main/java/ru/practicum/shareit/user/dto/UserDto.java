package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of= "name")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    //уникальный идентификатор пользователя
    private Long id;
    //имя или логин пользователя
    private String name;
    //Set уникальный элемент туда кладем все имеилы и ищем что это ошибка
    private String email;
}
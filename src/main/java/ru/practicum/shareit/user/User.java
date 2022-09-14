package ru.practicum.shareit.user;


import lombok.Data;

@Data
public class User {
    //уникальный идентификатор пользователя
    private Long id;
    //имя или логин пользователя
    private String name;
    //Set уникальный элемент туда кладем все имеилы и ищем что это ошибка
    private String email;
}

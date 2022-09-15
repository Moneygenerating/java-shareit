package ru.practicum.shareit.user.model;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //уникальный идентификатор пользователя
    private Long id;
    //имя или логин пользователя
    private String name;
    //Set уникальный элемент туда кладем все имеилы и ищем что это ошибка
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

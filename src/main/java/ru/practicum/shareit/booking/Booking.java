package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class Booking {
    //уникальный идентификатор бронирования
    private Long id;
    //дата и время начала бронирования
    private LocalDate start;
    //дата и время конца бронирования
    private LocalDate end;
    //вещь, которую пользователь бронирует
    private Item item;
    //пользователь, который осуществляет бронирование
    private User booker;
    //статус бронирования
    private StatusType status;
}

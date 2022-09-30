package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    //уникальный идентификатор бронирования
    private Long id;
    //дата и время начала бронирования
    private LocalDateTime start;
    //дата и время конца бронирования
    private LocalDateTime end;
    //вещь, которую пользователь бронирует
    private Item item;
    //пользователь, который осуществляет бронирование
    private User booker;
    //статус бронирования
    private StatusType status;
}

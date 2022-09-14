package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
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

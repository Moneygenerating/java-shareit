package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEnd
public class BookingDto {
    //уникальный идентификатор бронирования
    private Long id;
    //дата и время начала бронирования
    @FutureOrPresent
    private LocalDateTime start;
    //дата и время конца бронирования
    @Future
    private LocalDateTime end;
    //вещь, которую пользователь бронирует
    private Item item;
    //пользователь, который осуществляет бронирование
    private User booker;
    //статус бронирования
    private StatusType status;
}

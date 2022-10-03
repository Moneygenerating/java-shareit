package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.dto.CommentDto;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEnd
public class BookingDto {
    //уникальный идентификатор бронирования
    private Long id;
    private Long itemId;
    //дата и время начала бронирования
    @FutureOrPresent
    private LocalDateTime start;
    //дата и время конца бронирования
    @Future
    private LocalDateTime end;
    //статус бронирования
    private BookingState status;
    //пользователь, который осуществляет бронирование
    private UserNewDto booker;
    //вещь, которую пользователь бронирует
    private ItemNewDto item;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemNewDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long owner;
        private BookingShortDto lastBooking;
        private BookingShortDto nextBooking;
        private List<CommentDto> comments;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserNewDto {
        private Long id;
        private String name;
        private String email;
    }
}

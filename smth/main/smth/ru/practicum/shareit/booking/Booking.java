package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    //уникальный идентификатор бронирования
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //вещь, которую пользователь бронирует
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    //дата и время начала бронирования
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime start;
    //дата и время конца бронирования
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime end;
    //статус бронирования
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingState status;
    //пользователь, который осуществляет бронирование
    @Column(name = "booker_id")
    private Long bookerId;

}

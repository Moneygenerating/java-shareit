package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;
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
    //дата и время начала бронирования
    @Column(name = "start_date_time")
    private LocalDateTime start;
    //дата и время конца бронирования
    @Column(name = "end_date_time")
    private LocalDateTime end;
    //вещь, которую пользователь бронирует
    @OneToOne
    //@Column(name = "item_id")
    @JoinColumn(name = "items")
    private Item item;
    //пользователь, который осуществляет бронирование
    @OneToOne
    @JoinColumn(name = "users")
    private User booker;
    //статус бронирования
    private StatusType status;
}

package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    //уникальный идентификатор запроса
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //текст запроса, содержащий описание требуемой вещи
    private String description;
    //пользователь, создавший запрос
    @OneToOne
    @JoinColumn(name = "users")
    private User requester;
    //дата и время создания запроса
    private LocalDateTime created;
}

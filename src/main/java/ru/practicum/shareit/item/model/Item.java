package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    //уникальный идентификатор вещи
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //краткое название

    @Column(name = "name", nullable = false, unique = true)
    private String name;
    //развёрнутое описание
    private String description;
    //статус о том, доступна или нет вещь для аренды
    private Boolean available;
    //владелец вещи
    @ManyToOne (cascade=CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;
    //ссылка на соответствующий запрос
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item(Long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}

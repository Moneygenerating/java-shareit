package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    //@Column(name = "name_id", unique = true, nullable = false)
    @ManyToOne (cascade=CascadeType.ALL)
    @JoinColumn (name="users")
    private User owner;
    //ссылка на соответствующий запрос
    //@Column(name = "request_id")
    @OneToOne
    @JoinColumn(name = "item_requests")
    private ItemRequest request;

    public Item(String name, String description, Boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}

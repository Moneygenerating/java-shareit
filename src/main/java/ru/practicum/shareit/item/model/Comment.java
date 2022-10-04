package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    //@Column(name = "item_id", nullable = false)
    @ManyToOne(optional = false, cascade=CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private Item item;

    //@Column(name = "author_id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private User user;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}

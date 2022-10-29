package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment dtoToComment(CommentDto commentDto, Item item, User user, LocalDateTime dt) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                dt
        );
    }

    public static CommentDto commentToDto(Comment comment, String name) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getCreated(),
                name
        );
    }
}
package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

public interface UserDao {
    Set<UserDto> findAll();
    UserDto save(UserDto user);
}

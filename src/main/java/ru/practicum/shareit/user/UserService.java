package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

public interface UserService {
    Set<UserDto> getAllUsers();
    UserDto saveUser(UserDto user);
}

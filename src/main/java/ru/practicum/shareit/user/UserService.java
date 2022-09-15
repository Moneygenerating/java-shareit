package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto createUser(UserDto user);
    UserDto updateUser(Long userId, UserDto userDto);
    UserDto getUserById(Long userId);
    void deleteUserById(Long userId);

}

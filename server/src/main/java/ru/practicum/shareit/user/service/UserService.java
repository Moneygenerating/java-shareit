package ru.practicum.shareit.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(Pageable pageable);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);

    void deleteUserById(Long userId);

}

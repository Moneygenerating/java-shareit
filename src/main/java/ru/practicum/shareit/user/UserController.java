package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос user Get getAll");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Запрос user Get getUserById");
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody UserDto user) {
        log.info("Запрос user Post createNewUser");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated({Update.class}) @RequestBody UserDto userDto,
                              @PathVariable Long userId) {
        log.info("Запрос user Update updateUser");
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Запрос user Delete deleteUser");
        userService.deleteUserById(userId);
    }
}

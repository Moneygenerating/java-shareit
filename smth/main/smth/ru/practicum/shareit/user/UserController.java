package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.Update;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {


    @Autowired
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(value = "from", required = false, defaultValue = "0")
                                      int from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Запрос user Get getAll");
        return userService.getAllUsers(PageRequest.of(from / size, size));
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

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.service.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers(@RequestParam(value = "from", required = false, defaultValue = "0")
                                     @PositiveOrZero int from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Запрос user Get getAll");

        return userClient.getUsers(from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Запрос user Get getUserById");
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createNewUser(@RequestBody UserDto user) {
        log.info("Запрос user Post createNewUser");
        validateUser(user);
        return userClient.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated({Update.class}) @RequestBody UserDto userDto,
                              @PathVariable Long userId) {
        log.info("Запрос user Update updateUser");
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Запрос user Delete deleteUser");
        userClient.delete(userId);
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Отсутствует email");
        }

        if (!userDto.getEmail().endsWith(".com") || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Передан неверный email");
        }
    }
}

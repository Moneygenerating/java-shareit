package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Set<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto saveNewUser(@RequestBody UserDto user){
        return userService.saveUser(user);
    }

    /*
    @PatchMapping
    public UserDto updateUser(@Validated({Update.class}) @RequestBody UserDto userDto){
        return null;
    }

     */
}

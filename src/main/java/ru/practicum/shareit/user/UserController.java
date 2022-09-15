package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody UserDto user){
        return userService.createUser(user);
    }

    /*
    @PatchMapping
    public UserDto updateUser(@Validated({Update.class}) @RequestBody UserDto userDto){
        return null;
    }

     */
}

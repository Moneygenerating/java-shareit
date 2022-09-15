package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface UserDao {
    Set<User> findAll();
    User save(User user);
    User updateUser(Long userId, User user);
    User getUserById(Long userId);
    void deleteUser(Long userId);

}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public Set<UserDto> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public UserDto saveUser(UserDto user) {
        return userDao.save(user);
    }
}

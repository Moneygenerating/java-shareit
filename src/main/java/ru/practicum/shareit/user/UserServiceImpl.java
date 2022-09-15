package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto user) {
        User user1 = UserMapper.toUser(user);
        return UserMapper.toUserDto(userDao.save(user1));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userDao.updateUser(userId, user));

    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userDao.getUserById(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        userDao.deleteUser(userId);
    }
}

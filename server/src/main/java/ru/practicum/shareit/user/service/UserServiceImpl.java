package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UserDto createUser(UserDto userDto) {
        if (validateUser(userDto)) {
            User user = UserMapper.toUser(userDto); //id=null исправление
            return UserMapper.toUserDto(userRepository.save(user));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UserDto updateUser(Long userId, UserDto userDto) {
        UserDto userDtoCheck = getUserById(userId);
        if (userDtoCheck != null) {
            if (userDto.getName() != null) {
                userDtoCheck.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                validateUser(userDto);
                userDtoCheck.setEmail(userDto.getEmail());
            }

            User user = UserMapper.toUser(userDtoCheck);
            user.setId(userDtoCheck.getId());
            userRepository.save(user);
        }
        return userDtoCheck;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.getReferenceById(userId));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean validateUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Отсутствует email");
        }

        if (!userDto.getEmail().endsWith(".com") || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Передан неверный email");
        }
        return true;
    }

}

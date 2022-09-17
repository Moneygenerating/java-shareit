package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.errors.ConflictErrorException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {
    protected Set<User> users;
    long generatorId;

    public UserDaoImpl() {
        users = new HashSet<>();
        generatorId = 0;
    }

    @Override
    public List<User> findAll() {
        return users
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        validateUser(user);
        generatorId++;
        user.setId(generatorId);
        users.add(user);
        return users.stream()
                .filter(user2 -> user2.getId() == generatorId)
                .collect(Collectors.toList()).get(0);
    }

    @Override
    public User updateUser(Long userId, User user) {
        //вытаскиваем юзера
        User userForUpdate = users
                .stream()
                .filter(user1 -> Objects.equals(user1.getId(), userId))
                .collect(Collectors.toList())
                .get(0);

        User userForRemove = users
                .stream()
                .filter(user1 -> Objects.equals(user1.getId(), userId))
                .collect(Collectors.toList())
                .get(0);

        if (user.getName() != null) {
            userForUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            validateEmailForDouble(user);
            userForUpdate.setEmail(user.getEmail());

        }

        //удаляем
        users.remove(userForRemove);

        //добавляем
        users.add(userForUpdate);
        return userForUpdate;
    }

    @Override
    public User getUserById(Long userId) {
        User user1 = new User();
        for (User user : users) {
            if (Objects.equals(user.getId(), userId)) {
                user1 = user;
            }
        }
        return user1;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(getUserById(userId));
    }


    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            throw new ValidationException("Отсутствует email");
        }

        if (!user.getEmail().endsWith(".com") || !user.getEmail().contains("@")) {
            throw new ValidationException("Передан неверный email");
        }
        validateEmailForDouble(user);
    }

    public void validateEmailForDouble(User user) {
        users.forEach(user1 -> {
            if (Objects.equals(user1.getEmail(), user.getEmail())) {
                throw new ConflictErrorException("Подьзователь с таким email уже существует.");
            }
        });
    }

}

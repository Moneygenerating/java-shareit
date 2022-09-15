package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;
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
    public Set<User> findAll() {
        return users;
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

    public void validateUser(User user){
        if(user.getEmail() == null || user.getEmail().isBlank()|| user.getEmail().isEmpty()){
            throw new ValidationException("Отсутствует email");
        }

        if(!user.getEmail().endsWith(".com")||!user.getEmail().contains("@")){
            throw new ValidationException("Передан неверный email");
        }

    }

}

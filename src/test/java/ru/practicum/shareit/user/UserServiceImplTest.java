package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebServiceClientTest(UserService.class)
class UserServiceImplTest {

    @Autowired
    UserService userService;
    @MockBean
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
    }

    @Test
    void getAllUsers() {

        final PageImpl<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(PageRequest.ofSize(10)))
                .thenReturn(userPage);

        final List<UserDto> userDtoList = userService.getAllUsers(PageRequest.ofSize(10));

        assertNotNull(userDtoList);
        assertEquals(1, userDtoList.size());
        assertEquals(UserMapper.toUserDto(user), userDtoList.get(0));
    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void deleteUserById() {
    }
}
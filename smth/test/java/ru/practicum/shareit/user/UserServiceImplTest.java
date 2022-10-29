package ru.practicum.shareit.user;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ValidationException;
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
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@user.com");
        userDto = UserMapper.toUserDto(user);
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
    void createUserThisInvalidEmail() {
        UserDto userDtoNullEmail = new UserDto();

        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> userService.createUser(userDtoNullEmail));
        assertEquals(exception2.getMessage(), "Отсутствует email");

        userDtoNullEmail.setEmail("user1");
        ValidationException exception1 = assertThrows(ValidationException.class,
                () -> userService.createUser(userDtoNullEmail));
        assertEquals(exception1.getMessage(), "Передан неверный email");

        userDtoNullEmail.setEmail("");
        ValidationException exception0 = assertThrows(ValidationException.class,
                () -> userService.createUser(userDtoNullEmail));
        assertEquals(exception0.getMessage(), "Отсутствует email");
    }


    @Test
    void createUser() {
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        UserDto userDtoCreated = userService.createUser(userDto);
        assertEquals(userDtoCreated, userDto);
        assertEquals(userDtoCreated.getId(), userDto.getId());
        assertEquals(userDtoCreated.getName(), userDto.getName());
        assertEquals(userDtoCreated.getEmail(), userDto.getEmail());
    }

    @Test
    void updateSameUser() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);

        UserDto userDtoUpdated = userService.updateUser(1L, userDto);
        assertEquals(userDtoUpdated.getName(), userDto.getName());
        assertEquals(userDtoUpdated.getEmail(), userDto.getEmail());
    }

    @Test
    void getNotFoundErrorUser() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Не найден пользователь c таким id"));
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> userService.getUserById(2L));
        assertEquals(thrown.getMessage(), "Не найден пользователь c таким id");
    }

    @Test
    void updateUser() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        //update
        userDto.setEmail("testUpdated@mail.com");
        userDto.setName("testUpdatingName");

        UserDto userDtoUpdated = userService.updateUser(1L, userDto);
        assertEquals(userDtoUpdated.getName(), userDto.getName());
        assertEquals(userDtoUpdated.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUserWithInvalidMail() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        //update
        userDto.setEmail("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.updateUser(1L, userDto));
        assertEquals(exception.getMessage(), "Отсутствует email");
    }

    @Test
    void getUserById() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        assertEquals(userService.getUserById(1L).getId(), 1L);
    }

    @Test
    void deleteUserById() {
        User userForDelete = new User(2L, "user 2", "user2@user.com");
        when(userRepository.getReferenceById(1L)).thenReturn(userForDelete);
        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

}
package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@JsonTest
class UserTest {
    @Autowired
    private JacksonTester<User> json;

    @Test
    void testUser() throws IOException {
        User user = new User(1L, "name", "name@mail.ru");

        JsonContent<User> result = json.write(user);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(user.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
    }
}
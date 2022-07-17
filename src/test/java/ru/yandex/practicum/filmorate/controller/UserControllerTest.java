package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void validationTest() {
        final User user = new User();
        final UserValidation validation = new UserValidation();
        user.setEmail("andrew.bakalym@yandex.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(1993, 9, 25));
        validation.usersValidation(user);
        assertEquals(user.getEmail(), "andrew.bakalym@yandex.ru");
        assertEquals(user.getLogin(), "login");
        assertEquals(user.getName(), "name");
        assertEquals(user.getBirthday(), LocalDate.of(1993, 9, 25));
    }
}
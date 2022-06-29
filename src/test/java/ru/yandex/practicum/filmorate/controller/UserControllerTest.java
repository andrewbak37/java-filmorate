package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void usersValidation() {
        final UserController userController = new UserController();
        final User user = new User();
        user.setEmail("mail.ru");
        user.setLogin("");
        user.setBirthday(LocalDate.MAX);
        user.setId(-1);
        userController.usersValidation(user);
        assertEquals(user.getEmail(), ("@mail.ru"));
        assertEquals(user.getLogin(), ("login"));
        assertEquals(user.getBirthday(), (LocalDate.of(1993, 9, 25)));
        assertEquals(user.getId(), (1));
    }
}
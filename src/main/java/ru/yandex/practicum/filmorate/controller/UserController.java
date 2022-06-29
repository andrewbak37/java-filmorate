package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    public static int id = 0;
    LocalDateTime localDateTime = LocalDateTime.now();

    public static int getNewId() {
        return ++id;
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        usersValidation(user);
        user.setId(getNewId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.debug("Колличество пользователей: {}", users.size());
        return users.values();
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        usersValidation(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException();
        }
    }

    public void usersValidation(User user) {
        if (user.getEmail() == null || "".equals(user.getEmail()) || !user.getEmail().contains("@")) {
            log.error("должен содержать символ @ или лектронная почта не может быть пустой");
            throw new ValidationException();
        } else if (user.getLogin().isEmpty() && (user.getLogin().contains(" "))) {
            log.error("логин не может быть пустым и не должно быть пробелов");
            throw new ValidationException();
        } else if (user.getBirthday().isAfter(localDateTime.toLocalDate())) {
            log.error("Ты еще не родился");
            throw new ValidationException();
        } else if (user.getId() < 0) {
            log.error("id < 0");
            throw new ValidationException();
        } else if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }
    }
}
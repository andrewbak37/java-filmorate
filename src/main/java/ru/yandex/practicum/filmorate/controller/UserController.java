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
        return new ArrayList<>(users.values());
    }

    @PutMapping("/users")
    public void update(@RequestBody User user) {
        usersValidation(user);
        try {
            if (!users.containsKey(user.getId())) {
                users.put(user.getId(), user);
            }
        } catch (ValidationException v) {
            v.getMessage("Такого пользователя нет");
            log.error(v.getMessage());
        }
    }

    public void usersValidation(User user) {
        if (user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        } else if (user.getEmail().isEmpty()) {
            log.error("электронная почта не может быть пустой");
            throw new ValidationException();
        } else if (!(user.getEmail().contains("@"))) {
            log.error("должен содержать символ @");
            throw new ValidationException();
        } else if (user.getLogin().isEmpty()) {
            log.error("логин не может быть пустым");
            throw new ValidationException();
        } else if (user.getLogin().contains(" ")) {
            log.error("не должно быть пробелов");
            throw new ValidationException();
      } else if (user.getBirthday().isAfter(localDateTime.toLocalDate())) {
            log.error("Ты еще не родился");
            throw new ValidationException();
        }
    }
}
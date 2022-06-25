package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    public static long id = 0;

    public static long getNewId() {
        return ++id;
    }

    @PostMapping(value = "/user")
    public void create(@RequestBody User user) {
        user.setId(getNewId());
        users.put(getNewId(), user);
        log.info("Пользователь добавлен");
    }

    @GetMapping("/users")
    public Map<Long, User> findAll() {
        log.debug("Колличество пользователей: {}", users.size());
        return users;
    }

    @PutMapping("/user")
    public void update(@RequestBody User user) {
        try {
            if (!users.containsKey(user.getId())) {
                users.put(user.getId(), user);
            }
        } catch (ValidationException v) {
            v.getMessage("Такого пользователя нет");
            log.error(v.getMessage());
        }
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class UserController extends AbstractController<User> {
    UserValidation userValidation = new UserValidation();

    @Override
    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User model) {
        userValidation.usersValidation(model);
        return super.create(model);
    }

    @Override
    @GetMapping(value = "/users")
    public Collection<User> findAll() {
        return super.findAll();
    }

    @Override
    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User model) {
        userValidation.usersValidation(model);
        return super.update(model);
    }
}

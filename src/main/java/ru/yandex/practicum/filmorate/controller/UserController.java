package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    @GetMapping(value = "/users")
    public Collection<User> findAll() {
        return service.findAll();
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return service.update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable("id") int id,
                           @PathVariable("friendId") int friendId) {
        service.addFriends(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id,
                             @PathVariable("friendId") int friendId) {
        service.deleteFriends(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public Set<User> getAllFriends(@PathVariable("id") int id) {
       return service.getAllFriends(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getFriendsManual(@PathVariable("id") int id, @PathVariable("otherId") int otherId){
        return service.getAllCommonFriends(id, otherId);
    }

    @GetMapping(value = "/users/{id}")
    public User getUserId(@PathVariable("id") int id) {
        return service.get(id);
    }

}

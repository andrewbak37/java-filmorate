package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    public int id = 0;

    private final Map<Integer, User> mapUser = new HashMap<>();

    public int getNewId() {
        return ++id;
    }

    UserValidation userValidation = new UserValidation();

    public Map<Integer, User> getMapUser() {
        return mapUser;
    }

    @Override
    public User create(User user) {
        userValidation.usersValidation(user);
        user.setId(getNewId());
        mapUser.put(user.getId(), user);
        log.info("Объект добавлен");
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return mapUser.values();
    }

    @Override
    public User update(User user) {
        userValidation.usersValidation(user);
        if (mapUser.containsKey(user.getId())) {
            mapUser.put(user.getId(), user);
            return user;
        } else {
            log.error("Ключа нет");
            throw new ValidationException();
        }
    }

    @Override
    public User getUserById(int id) {
        return mapUser.get(id);
    }

    public void addFriends(User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void deleteFriends(User user, User friend) {
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }
}


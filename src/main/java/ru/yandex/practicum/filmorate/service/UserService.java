package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriends(int id, int friendId) {
        checkUnknownUser(friendId);
        User user = inMemoryUserStorage.getUserById(id);
        User friend = inMemoryUserStorage.getUserById(friendId);
        inMemoryUserStorage.addFriends(user, friend);
    }

    public void deleteFriends(int id, int friendId) {
        User user = inMemoryUserStorage.getUserById(id);
        User friend = inMemoryUserStorage.getUserById(friendId);
        inMemoryUserStorage.deleteFriends(user, friend);
    }

    public List<User> getAllCommonFriends(int id, int secondId) {
        Set<Integer> user = inMemoryUserStorage.getUserById(id).getFriends();
        Set<Integer> userSecond = inMemoryUserStorage.getUserById(secondId).getFriends();

        List<Integer> friends = user.stream().distinct().filter(userSecond :: contains).collect(Collectors.toList());
        List<User> commonFriends = new ArrayList<>();
        for (Integer i : friends) {
            commonFriends.add(inMemoryUserStorage.getUserById(i));
        }
        return commonFriends;
    }

    public Set<User> getAllFriends(int id) {
        Set<Integer> friends = inMemoryUserStorage.getMapUser().get(id).getFriends();
        Set<User> userSet = new HashSet<>();
        friends.forEach(friend -> userSet.add(inMemoryUserStorage.getMapUser().get(friend)));
        return userSet;

    }

    public User get(int userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        if (user == null) {
            log.info("Нет");
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        } else return user;
    }

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    public User update(User user) {
        if (user.getId() < 0) {
            throw new NotFoundException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return inMemoryUserStorage.update(user);
    }

    public void checkUnknownUser(int idUser) {
        if (idUser < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }
}

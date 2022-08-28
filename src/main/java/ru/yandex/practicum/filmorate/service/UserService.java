package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
@Data

public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {

        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;

    }

    public void addFriends(int id, int friendId) {
        checkUnknownUser(friendId);
        friendshipStorage.addFriend(id, friendId);
    }

    public void deleteFriends(int id, int friendId) {
        friendshipStorage.deleteFriend(id, friendId);
    }

    public List<User> getAllCommonFriends(int id, int secondId) {
        return friendshipStorage.getAllCommonFriends(id, secondId);

    }

    public List<User> getAllFriends(int id) {
        return friendshipStorage.getAllFriends(id);
    }

    public void confirmFriend(int id, int notConfirmFriendId) {
        friendshipStorage.confirmFriend(id, notConfirmFriendId);
    }

    public User getUserById(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.info("Такого юзера нет");
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        } else return user;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);

    }

    public Collection<User> getAllUser() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) {
        if (user.getId() < 0) {
            throw new NotFoundException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return userStorage.updateUser(user);
    }

    public void checkUnknownUser(int idUser) {
        if (idUser < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }
}

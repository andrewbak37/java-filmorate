package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final UserValidation userValidation;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        userValidation.usersValidation(user);
        String addUser = "INSERT INTO USERS(EMAIL, LOGIN, BIRTHDAY, USER_NAME) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(addUser, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getName());
            return stmt;
        }, keyHolder);
        int user_id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(user_id);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        SqlRowSet userRowsUsers = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        Map<Integer, User> userMap = new HashMap<>();

        while (userRowsUsers.next()) {
            User user = new User();
            int user_id = userRowsUsers.getInt("USER_ID");
            SqlRowSet sqlRowSetFriends = jdbcTemplate.queryForRowSet("SELECT * FROM USER_FRIENDS WHERE USER_ID = ?", user_id);
            makeRequestGetUser(user, userRowsUsers, sqlRowSetFriends);
            userMap.put(user_id, user);
        }
        return userMap.values();
    }

    @Override
    public User updateUser(User user) {
        userValidation.usersValidation(user);
        String update = "UPDATE USERS SET LOGIN = ?, USER_NAME = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(update,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User getUserById(int id) {
        if (id <= 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        SqlRowSet rowSetUsers = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        SqlRowSet rowSetFriends = jdbcTemplate.queryForRowSet("SELECT  * FROM USER_FRIENDS WHERE  USER_ID = ?", id);

        User user = new User();
        if (rowSetUsers.next()) {
            makeRequestGetUser(user, rowSetUsers, rowSetFriends);
        }
        return user;
    }

    @Override
    public void deleteUser(int id) {
        userValidation.usersValidation(getUserById(id));
        String deleteUser = "DELETE FROM USERS WHERE USER_ID = ?";
        String deleteUserFromFriends = "DELETE FROM USER_FRIENDS WHERE USER_ID = ?";

        jdbcTemplate.update(deleteUser, getUserById(id).getId());
        jdbcTemplate.update(deleteUserFromFriends, getUserById(id).getId());
    }

    private void selectAllFromUsers(User user, SqlRowSet userRowsUsers) {
        user.setId(userRowsUsers.getInt("USER_ID"));
        user.setName(Objects.requireNonNull(userRowsUsers.getString("USER_NAME")));
        user.setEmail(Objects.requireNonNull(userRowsUsers.getString("EMAIL")));
        user.setLogin(Objects.requireNonNull(userRowsUsers.getString("LOGIN")));
        user.setBirthday(Objects.requireNonNull(userRowsUsers.getDate("BIRTHDAY")).toLocalDate());
    }

    private void makeRequestGetUser(User user, SqlRowSet userRowsUsers, SqlRowSet userRowsFriends) {
        selectAllFromUsers(user, userRowsUsers);

        List<Integer> friendsMap = new ArrayList<>();
        while (userRowsFriends.next()) {
            friendsMap.add(userRowsFriends.getInt("FRIEND_ID"));
        }
        user.setFriends(friendsMap);
    }
}


package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int id, int friendId) {
        String addFriends = "INSERT INTO USER_FRIENDS(USER_ID, FRIEND_ID, FRIEND_STATUS) VALUES (?,?,?)";
        jdbcTemplate.update(addFriends, friendId, id, FALSE);
        jdbcTemplate.update(addFriends, id, friendId, TRUE);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String removeFriend = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(removeFriend, friendId, userId);
        jdbcTemplate.update(removeFriend, userId, friendId);
    }

    @Override
    public List<User> getAllCommonFriends(int userId, int friendId) {
        if (userId == 0 && friendId == 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        SqlRowSet sqlRowSetGetFriendsUser = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID FROM USER_FRIENDS " +
                "WHERE USER_ID = ?", userId);
        List<User> commonFriends = new ArrayList<>();
        while (sqlRowSetGetFriendsUser.next()) {
            int id = sqlRowSetGetFriendsUser.getInt("FRIEND_ID");

            SqlRowSet sqlRowSetGetFriendsFriend = jdbcTemplate.queryForRowSet("SELECT * FROM USER_FRIENDS " +
                    "WHERE USER_ID = ? AND FRIEND_ID = ?", friendId, id);

            if (sqlRowSetGetFriendsFriend.next()) {
                User user = new User();
                getUser(user, id);
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }


    @Override
    public List<User> getAllFriends(int id) {
        SqlRowSet rowSetGet = jdbcTemplate.queryForRowSet("SELECT * FROM USER_FRIENDS WHERE USER_ID = ? " +
                "AND FRIEND_STATUS = TRUE", id);
        List<User> friends = new ArrayList<>();
        while (rowSetGet.next()) {
            int friendId = rowSetGet.getInt("FRIEND_ID");
            User friend = new User();
            getUser(friend, friendId);
            friends.add(friend);
        }
        return friends;
    }

    @Override
    public void confirmFriend(int id, int notConfirmFriendId) {
        String confirmFriend = "UPDATE USER_FRIENDS SET FRIEND_STATUS = TRUE WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(confirmFriend, id, notConfirmFriendId);
    }

    private void getUser(User user, int id) {
        SqlRowSet userRowsUsers = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        if (userRowsUsers.next()) {
            user.setId(userRowsUsers.getInt("USER_ID"));
            user.setLogin(Objects.requireNonNull(userRowsUsers.getString("LOGIN")));
            user.setName(Objects.requireNonNull(userRowsUsers.getString("USER_NAME")));
            user.setEmail(Objects.requireNonNull(userRowsUsers.getString("EMAIL")));
            user.setBirthday(Objects.requireNonNull(userRowsUsers.getDate("BIRTHDAY")).toLocalDate());
        }
        SqlRowSet userRowsFriends = jdbcTemplate.queryForRowSet("SELECT * FROM USER_FRIENDS WHERE USER_ID = ? ", id);

        List<Integer> friendsList = new ArrayList<>();
        while (userRowsFriends.next()) {
            friendsList.add(userRowsFriends.getInt("FRIEND_ID"));
        }
        user.setFriends(friendsList);
    }
}

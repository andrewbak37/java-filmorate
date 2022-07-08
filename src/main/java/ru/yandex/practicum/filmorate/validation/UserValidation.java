package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;


@Slf4j
public class UserValidation extends User {
    public LocalDateTime localDateTime = LocalDateTime.now();

    public void usersValidation(User user) {
        if (user.getEmail().isEmpty() || (!(user.getEmail().contains("@")))) {
            log.error("электронная почта не может быть пустой и должена содержать символ @");
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
        } else if (user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }
}

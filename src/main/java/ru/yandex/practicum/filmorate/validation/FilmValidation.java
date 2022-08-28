package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;

@Slf4j
@Component
public class FilmValidation extends Film {
    public final int MAX_VALUE = 200;
    public LocalDate date = LocalDate.of(1895, 12, 28);


    public void filmsValidation(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Имя пустое");
            throw new ValidationException();
        } else if (film.getDescription().length() > MAX_VALUE) {
            log.error("Длина описание не может быть больше 200 символов");
            throw new ValidationException();
        } else if (film.getReleaseDate().isBefore(date)) {
            log.error("дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException();
        } else if (film.getDuration() < 0) {
            log.error("продолжительность фильма должна быть положительной");
            throw new ValidationException();
        } else if (film.getMpa().getName() == null && film.getMpa().getId() == null) {
            log.error("MPA не должен быть равен null");
            throw new ValidationException();

        }
    }
}

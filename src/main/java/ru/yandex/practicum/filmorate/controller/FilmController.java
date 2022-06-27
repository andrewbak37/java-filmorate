package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;


@Slf4j
@RestController
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    public static int id = 0;
    public static final int MAX_VALUE = 200;
    public LocalDate date = LocalDate.of(1895, 12, 28);

    public static int getNewId() {
        return ++id;
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        filmsValidation(film);
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return film;

    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Колличество фильмов: {}", films.size());
        log.error("Список пуст");
        return films.values();
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            filmsValidation(film);
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException();
        }
    }


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

        }
    }
}


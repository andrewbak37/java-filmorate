package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    public static long id = 0;

    public static long getNewId() {
        return ++id;
    }

    @PostMapping(value = "/film")
    public Long create(@RequestBody Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return film.getId();
    }

    @GetMapping("/films")
    public Map<Long, Film> findAll() {
        log.debug("Колличество фильмов: {}", films.size());
        return films;
    }

    @PutMapping("/film")
    public Long update(@RequestBody Film film) {
        try {
            if (!films.containsKey(film.getId())) {
                films.put(film.getId(), film);
            }
        } catch (ValidationException v) {
            v.getMessage("Такого фильма нет");
            log.error(v.getMessage());
        }
        return film.getId();
    }
}

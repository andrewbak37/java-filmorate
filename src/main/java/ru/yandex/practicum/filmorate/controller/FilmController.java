package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class FilmController {

    final private FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return service.create(film);
    }

    @GetMapping(value = "/films")
    public Collection<Film> findAll() {
        return service.findAll();
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return service.update(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        return service.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") int id,
                           @PathVariable("userId") int userId) {
        return service.deleteLike(id, userId);
    }

    @GetMapping(value = "/films/popular")
    public Collection<Film> getPopularFilm(@RequestParam(defaultValue = "10") int count) {
        return service.popularFilm(count);
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable("id") int id) {
        return service.getFilmById(id);
    }
}


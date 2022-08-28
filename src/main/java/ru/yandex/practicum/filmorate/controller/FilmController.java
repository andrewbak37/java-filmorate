package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private final FilmService service;

    @Autowired
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
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id,
                           @PathVariable("userId") int userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    @ResponseBody
    public Collection<Film> getPopularFilms(@RequestParam(required = false) String count) {
        if (count == null) {
            return service.popularFilm(10);
        }
        if (Integer.parseInt(count) <= 0) {
            throw new RuntimeException("Incorrect count");
        }
        return service.popularFilm(Integer.parseInt(count));
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable("id") int id) {
        return service.getFilmById(id);
    }

    @GetMapping(value = "/genres")
    @ResponseBody
    public List<Genre> getGenres() {
        return service.getGenres();
    }

    @GetMapping(value = "/genres/{id}")
    @ResponseBody
    public Genre getGenre(@PathVariable int id) {
        return service.getGenreById(id);
    }

    @GetMapping(value = "/mpa")
    @ResponseBody
    public List<MPA> getRates() {
        return service.getMPA();
    }

    @GetMapping(value = "/mpa/{id}")
    @ResponseBody
    public MPA getRate(@PathVariable int id) {
        return service.getMpaById(id);
    }

}


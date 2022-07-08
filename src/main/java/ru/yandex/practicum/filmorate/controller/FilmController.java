package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class FilmController extends AbstractController<Film> {
    FilmValidation filmValidation = new FilmValidation();

    @Override
    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film model) {
        filmValidation.filmsValidation(model);
        return super.create(model);
    }

    @Override
    @GetMapping(value = "/films")
    public Collection<Film> findAll() {
        return super.findAll();
    }

    @Override
    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film model) {
        filmValidation.filmsValidation(model);
        return super.update(model);
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {

    @Test
    void validationTest() {
        final FilmController filmController = new FilmController();
        final Film film = new FilmValidation();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(113);
        filmController.filmValidation.filmsValidation(film);
        assertEquals(film.getName(), "name");
        assertEquals(film.getDescription(), "description");
        assertEquals(film.getReleaseDate(), LocalDate.of(2000, 1, 1));
        assertEquals(film.getDuration(), 113);
    }

}
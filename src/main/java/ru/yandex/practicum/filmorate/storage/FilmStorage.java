package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film createFilm(Film film);

    Collection<Film> getAllFilms();

    Film update (Film film);

    Film getById(int id);
}

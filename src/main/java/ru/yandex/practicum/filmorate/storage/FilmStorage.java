package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    List<Film> getAllFilms();

    Film update (Film film);

    Film getById(int id);

    List<Film> getPopularFilms(int count);


}

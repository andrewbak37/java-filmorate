package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    public int id = 0;

    private final Map<Integer, Film> mapFilm = new HashMap<>();

    FilmValidation validation = new FilmValidation();

    public int getNewId() {
        return ++id;
    }

    public Map<Integer, Film> getMapFilm() {
        return mapFilm;
    }

    @Override
    public Film create(Film film) {
        validation.filmsValidation(film);
        film.setId(getNewId());
        mapFilm.put(film.getId(), film);
        log.info("Объект добавлен");
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return mapFilm.values();
    }

    @Override
    public Film update(Film film) {
        validation.filmsValidation(film);
        if (mapFilm.containsKey(film.getId())) {
            mapFilm.put(film.getId(), film);
            return film;
        } else {
            log.error("Ключа нет");
            throw new ValidationException();
        }
    }
}

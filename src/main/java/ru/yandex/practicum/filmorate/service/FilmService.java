package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;


@Service
public class FilmService {

    final private InMemoryFilmStorage inMemoryFilmStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film addLike(int filmId, int userId) {
        inMemoryFilmStorage.getMapFilm().get(filmId).getUserLikes().add(userId);
        return inMemoryFilmStorage.getMapFilm().get(filmId);
    }

    public Film deleteLike(int filmId, int userId) {
        checkUnknownFilm(userId);
        inMemoryFilmStorage.getMapFilm().get(filmId).getUserLikes().remove(userId);
        return inMemoryFilmStorage.getMapFilm().get(filmId);
    }

    public Film create (Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    public Film update(Film film) {
        checkUnknownFilm(film.getId());
        return inMemoryFilmStorage.update(film);
    }

    public Film getFilmById(int id) {
        checkUnknownFilm(id);
        return inMemoryFilmStorage.getMapFilm().get(id);
    }

    public Collection<Film> popularFilm(int count) {
        return inMemoryFilmStorage.getMapFilm().values().stream()
                .sorted(Comparator.comparing(e-> -e.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void checkUnknownFilm(int idFilm) {
        if (idFilm < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }
}








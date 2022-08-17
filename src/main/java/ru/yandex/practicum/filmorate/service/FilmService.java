package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.List;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {

        this.filmStorage = storage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void addLike(int filmId, int userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        checkUnknownFilm(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    public Film create(Film film) {
        return filmStorage.createFilm(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    public Film update(Film film) {
        checkUnknownFilm(film.getId());
        return filmStorage.update(film);
    }

    public Film getFilmById(int id) {
        checkUnknownFilm(id);
        return filmStorage.getById(id);
    }

    public Collection<Film> popularFilm(int count) {
        return likeStorage.getPopularFilms(count);
    }

    public void checkUnknownFilm(int idFilm) {
        if (idFilm < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<MPA> getMPA() {
        return mpaStorage.getMPA();
    }

    public MPA getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

}








package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikeStorage {
    Collection<Film> getPopularFilms(int count);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

}

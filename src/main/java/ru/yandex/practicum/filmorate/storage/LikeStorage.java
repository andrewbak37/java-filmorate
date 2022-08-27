package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface LikeStorage {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

}

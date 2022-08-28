package ru.yandex.practicum.filmorate.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
@Service
@Data
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeDbStorage likeDbStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        MPA mpa = new MPA();
        film.setId(rs.getInt("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getDate("RELEASEDATE").toLocalDate());
        film.setDuration(rs.getInt("DURATION"));
        film.setMpa(mpa);
        mpa.setId(rs.getInt("MPA_ID"));
        mpa.setName(rs.getString("MPA_NAME"));
        film.setRate(likeDbStorage.getLikesCount(rs.getInt("FILM_ID"))); // TODO: 22.08.2022 Возможно тут ошибка и нужно в таблицу FILM_LIKE добавить id
        film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
        return film;
    }
}

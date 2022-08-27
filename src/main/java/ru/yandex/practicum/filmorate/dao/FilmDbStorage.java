package ru.yandex.practicum.filmorate.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Data
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeDbStorage likeDbStorage;

    @Override
    public Film createFilm(Film film) throws FilmNotFound {
        film.setRate(0);
        String sql = "INSERT INTO FILMS (" +
                "FILM_NAME, " +
                "RELEASEDATE, " +
                "DESCRIPTION, " +
                "DURATION, " +
                "MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"FILM_ID"});
            ps.setString(1, film.getName());
            LocalDate realeaseDate = film.getReleaseDate();
            if (realeaseDate == null) {
                ps.setNull(2, Types.DATE);
            } else {
                ps.setDate(2, Date.valueOf(realeaseDate));
            }
            ps.setString(3, film.getDescription());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
      // setFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql1 = "SELECT * FROM FILMS " +
                "JOIN MPA ON FILMS.MPA_ID=MPA.MPA_ID";
        return jdbcTemplate.query(sql1, new FilmRowMapper(genreDbStorage, mpaDbStorage, likeDbStorage));
    }

    @Override
    public Film update(Film film) throws FilmNotFound {
       // film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));
        getById(film.getId());
        String sql = "UPDATE FILMS SET " +
                "FILM_NAME = ?, " +
                "RELEASEDATE = ?, " +
                "DESCRIPTION = ?, " +
                "DURATION = ?, " +
                "MPA_ID = ? " +
                "WHERE FILM_ID = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

      //  setFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film getById(int id) throws FilmNotFound {
        try {
            String sql = "SELECT * FROM FILMS " +
                    "JOIN MPA ON FILMS.MPA_ID=MPA.MPA_ID " +
                    "WHERE FILMS.FILM_ID = ?";
            return jdbcTemplate.queryForObject(sql, new FilmRowMapper(genreDbStorage, mpaDbStorage, likeDbStorage), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFound(" ");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT TOP ? * FROM FILMS " +
                "JOIN MPA ON FILMS.MPA_ID=MPA.MPA_ID " +
                "LEFT JOIN FILM_LIKES L ON FILMS.FILM_ID = L.FILM_ID " +
                "GROUP BY FILMS.FILM_ID, " +
                "L.USER_ID " +
                "ORDER BY COUNT(USER_ID) DESC";
        if (count != 0) {
            return jdbcTemplate.query(sql, new FilmRowMapper(genreDbStorage, mpaDbStorage, likeDbStorage), count);
        } else {
            return jdbcTemplate.query(sql, new FilmRowMapper(genreDbStorage, mpaDbStorage, likeDbStorage), 10);
        }
    }

//    private Genre getFilmGenresById(int id) {
//        SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?", id);
//        Genre genre = new Genre();
//
//        if (filmRowsGenres.next()) {
//            genre.setId(filmRowsGenres.getInt("GENRE_ID"));
//            genre.setName(filmRowsGenres.getString("GENRE"));
//        }
//        return genre;
//    }

    private void deleteFilm(Film film) {
        String deleteAllLikeFilm = "DELETE FROM FILM_LIKES WHERE FILM_ID = ?";
        String deleteAllGenreFilm = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        String deleteAllMpaFilm = "DELETE FROM FILM_MPA WHERE FILM_ID = ?";
        String deleteFilm = "DELETE FROM FILMS WHERE FILM_ID = ?";

        jdbcTemplate.update(deleteAllLikeFilm, film.getId());
        jdbcTemplate.update(deleteAllGenreFilm, film.getId());
        jdbcTemplate.update(deleteAllMpaFilm, film.getId());
        jdbcTemplate.update(deleteFilm, film.getId());
    }

    public void setFilmGenres(int filmId, List<Genre> genres) throws FilmNotFound {
        String sqlCheck = "SELECT COUNT(*) FROM FILM_GENRES WHERE FILM_ID = ?";
        Integer check = jdbcTemplate.queryForObject(sqlCheck, Integer.class, filmId);
        if (check == 0) {
            for (Genre genre : genres) {
                String sqlInsert = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sqlInsert, filmId, genre.getId());
            }
        } else {
            String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlDelete, filmId);
            for (Genre genre : genres) {
                String sqlMerge = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sqlMerge, filmId, genre.getId());
            }
        }
    }
}
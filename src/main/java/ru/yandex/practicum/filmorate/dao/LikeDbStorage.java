package ru.yandex.practicum.filmorate.dao;

import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;

@Data
@Component
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getPopularFilms(int count) {
        SqlRowSet filmRowsFilms = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS ORDER BY RATE DESC LIMIT ?"
                , count);
        List<Film> films = new ArrayList<>();

        while (filmRowsFilms.next()) {
            Film film = new Film();
            int filmId = filmRowsFilms.getInt("FILM_ID");

            SqlRowSet filmRowsMpa = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_MPA WHERE FILM_ID = ?", filmId);

            SqlRowSet filmRowsGenre = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_GENRES " +
                    "WHERE FILM_ID = ?", filmId);

            SqlRowSet filmRowsLiked = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_LIKES WHERE FILM_ID = ?"
                    , filmId);

            makeRequestGetFilm(film, filmRowsFilms, filmRowsMpa, filmRowsLiked, filmRowsGenre);
            films.add(film);
        }
        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_LIKES WHERE FILM_ID = ?" +
                "AND USER_ID = ?", filmId, userId);

        boolean f = false;
        while (rowSet.next()) {
            f = true;
        }
        if (f) {
            return;
        }

        int temp = getRate(userId, filmId) + 1;
        String sqlQueryFilmsLike = "INSERT INTO FILM_LIKES(FILM_ID, USER_ID) VALUES (?, ?)";
        String sqlQueryFilmsCountLike = "UPDATE FILMS SET RATE = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryFilmsCountLike, temp, filmId);
        jdbcTemplate.update(sqlQueryFilmsLike, filmId, userId);

    }

    @Override
    public void deleteLike(int filmId, int userId) {
        int temp = getRate(userId, filmId) - 1;

        String deleteAllLikeFilm = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        String sqlQueryFilmsCountLike = "UPDATE FILMS SET RATE = ? WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQueryFilmsCountLike, temp, filmId);
        jdbcTemplate.update(deleteAllLikeFilm, filmId, userId);
    }

    private int getRate(long userId, long filmId) {
        String sqlQueryFilmRemoveLiked = "DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQueryFilmRemoveLiked, userId, filmId);

        String sqlQueryFilm = "SELECT RATE FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet sqlQueryFilmRow = jdbcTemplate.queryForRowSet(sqlQueryFilm,
                filmId);
        int rate = 0;
        if (sqlQueryFilmRow.next()) {
            rate = sqlQueryFilmRow.getInt("RATE");
        }
        return rate;
    }

    private List<Genre> getGenres(SqlRowSet filmRowsGenre) {
        List<Genre> genres = new ArrayList<>();
        while (filmRowsGenre.next()) {
            Genre genre = new Genre();
            genre.setId(filmRowsGenre.getInt("GENRE_ID"));
            genres.add(genre);
        }
        return genres;
    }

    private void selectAllFromFilms(Film film, SqlRowSet filmRowsFilms) {
        film.setId(filmRowsFilms.getInt("FILM_ID"));
        film.setName(Objects.requireNonNull(filmRowsFilms.getString("FILM_NAME")));
        film.setDescription(Objects.requireNonNull(filmRowsFilms.getString("DESCRIPTION")));
        film.setReleaseDate(Objects.requireNonNull(filmRowsFilms.getDate("RELEASEDATE")).toLocalDate());
        film.setDuration(filmRowsFilms.getInt("DURATION"));
        film.setRate(filmRowsFilms.getInt("RATE"));
    }

    private void makeRequestGetFilm(Film film, SqlRowSet filmRowsFilms, SqlRowSet filmRowsMpa, SqlRowSet filmRowsLiked
            , SqlRowSet filmRowsGenre) {

        selectAllFromFilms(film, filmRowsFilms);

        if (filmRowsMpa.next()) {
            MPA mpa = new MPA();
            mpa.setId(filmRowsMpa.getInt("MPA_ID"));
            film.setMpa(mpa);
        }

        Set<Integer> usersId = new HashSet<>();
        while (filmRowsLiked.next()) {
            usersId.add(filmRowsLiked.getInt("USER_ID"));
        }
        film.setUsersId(usersId);

        film.setGenres(getGenres(filmRowsGenre));

        for (int i = 0; i < film.getGenres().size(); i++) {
            SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?",
                    film.getGenres().get(i).getId());

            if (filmRowsGenres.next()) { // TODO: 17.08.2022
                film.getGenres().get(i).setName(filmRowsGenres.getString("GENRE_NAME"));
            }
        }

        String sqlQueryMpa = "SELECT * FROM MPA WHERE MPA_ID = ?";
        SqlRowSet filmRowsMpaId = jdbcTemplate.queryForRowSet(sqlQueryMpa,
                film.getMpa().getId());

        if (filmRowsMpaId.next()) {
            film.getMpa().setName(filmRowsMpaId.getString("MPA_NAME"));
        }
    }
}

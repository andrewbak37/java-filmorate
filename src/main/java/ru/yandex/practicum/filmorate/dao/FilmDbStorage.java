package ru.yandex.practicum.filmorate.dao;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component
@Data
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmValidation filmValidation;

    @Override
    public Film createFilm(Film film) {
        filmValidation.filmsValidation(film);
        if (film.getMpa() == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        int filmId = 0;
        String sqlQueryFilms = "INSERT INTO FILMS(FILM_NAME, RELEASEDATE, DESCRIPTION, DURATION, RATE) " +
                "VALUES (?, ?, ?, ?, ?)";

        String sqlQueryFilmMpa = "INSERT INTO FILM_MPA(FILM_ID, MPA_ID) VALUES (?, ?)";

        String sqlQueryFilmGenre = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES (?, ?)";

        String sqlQueryMpa = "SELECT * FROM MPA WHERE MPA_ID = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQueryMpa, film.getMpa().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQueryFilms, new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, String.valueOf(Date.valueOf(film.getReleaseDate())));
            statement.setString(3, film.getDescription());
            statement.setString(4, String.valueOf(film.getDuration()));
            statement.setString(5, String.valueOf(film.getRate()));
            return statement;
        }, keyHolder);

        filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        jdbcTemplate.update(sqlQueryFilmMpa, filmId, film.getMpa().getId());

        if (film.getGenres() != null) {
            for (int i = 0; i < film.getGenres().size(); i++) {
                jdbcTemplate.update(sqlQueryFilmGenre, filmId, film.getGenres().get(i).getId());
            }

            for (int i = 0; i < film.getGenres().size(); i++) {
                SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?",
                        film.getGenres().get(i).getId());
                if (filmRowsGenres.next()) {
                    film.getGenres().get(i).setName(filmRowsGenres.getString("GENRE_ID"));
                }
            }
        }
        if (rowSet.next()) {
            film.getMpa().setName(rowSet.getString("MPA_ID"));
        }
        film.setId(filmId);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT  * FROM FILMS");

        Map<Integer, Film> filmMap = new HashMap<>();

        while (sqlRowSet.next()) {
            Film film = new Film();
            int filmId = sqlRowSet.getInt("FILM_ID");

            SqlRowSet filmRowMpa = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_MPA WHERE  FILM_ID = ?", filmId);
            SqlRowSet filmRowsGenre = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_GENRES WHERE FILM_ID = ?", filmId);
            SqlRowSet filmRowsLike = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_LIKES WHERE FILM_ID = ?", filmId);

            makeRequestGetFilm(film, sqlRowSet, filmRowMpa, filmRowsLike, filmRowsGenre);
            filmMap.put(filmId, film);
        }
        return filmMap.values();
    }

    @Override
    public Film update(Film film) {
        filmValidation.filmsValidation(film);
        int filmId = film.getId();
        if (filmId < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        String sqlQueryFilms = "UPDATE FILMS SET FILM_NAME = ?, RELEASEDATE = ?, DESCRIPTION = ?,  DURATION = ?" +
                ", RATE = ? WHERE FILM_ID = ?";

        String sqlQueryFilmMpa = "UPDATE FILM_MPA SET MPA_ID = ? WHERE FILM_ID = ?";

        String deleteAllRecordsFilmGenreOfFilm = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";

        String sqlQueryFilmGenre = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES (?, ?)";

        String sqlQueryMpa = "SELECT * FROM MPA WHERE MPA_ID = ?";
        SqlRowSet filmRowsMpaId = jdbcTemplate.queryForRowSet(sqlQueryMpa,
                film.getMpa().getId());

        if (filmRowsMpaId.next()) {
            film.getMpa().setName(filmRowsMpaId.getString("MPA_NAME")); // TODO: 17.08.2022  
        }

        jdbcTemplate.update(sqlQueryFilms,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getRate(),
                filmId);

        jdbcTemplate.update(sqlQueryFilmMpa, film.getMpa().getId(), filmId);
        jdbcTemplate.update(deleteAllRecordsFilmGenreOfFilm, filmId);

        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>();

            for (int i = 0; i < film.getGenres().size(); i++) {
                boolean f = false;
                for (int y = i + 1; y < film.getGenres().size(); y++) {
                    if (film.getGenres().get(i).getId() == film.getGenres().get(y).getId()) {
                        f = true;
                    }
                }
                if (!f) {
                    Genre genre = new Genre();
                    genre.setId(film.getGenres().get(i).getId());
                    genres.add(genre);
                }
            }

            Comparator<Genre> genreComparator =
                    Comparator.comparing(Genre::getId);
            genres.sort(genreComparator);
            film.setGenres(genres);

            for (int i = 0; i < film.getGenres().size(); i++) {
                jdbcTemplate.update(sqlQueryFilmGenre, filmId, film.getGenres().get(i).getId());
            }
            for (int i = 0; i < film.getGenres().size(); i++) {
                SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?",
                        film.getGenres().get(i).getId());

                if (filmRowsGenres.next()) {
                    film.getGenres().get(i).setName(filmRowsGenres.getString("GENRE_NAME"));
                }
            }
        }
        return film;
    }

    @Override
    public Film getById(int id) {
        if (id < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        SqlRowSet filmRowsFilms = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE FILM_ID = ?", id);
        SqlRowSet filmRowsMpa = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_MPA WHERE FILM_ID = ?", id);
        SqlRowSet filmRowsGenre = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_GENRES WHERE FILM_ID = ?", id);
        SqlRowSet filmRowsLike = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_LIKES WHERE FILM_ID = ?", id);

        Film film = new Film();

        if (filmRowsFilms.next()) {
            makeRequestGetFilm(film, filmRowsFilms, filmRowsMpa, filmRowsLike, filmRowsGenre);
        }
        return film;
    }

    private Genre getFilmGenresById(int id) {
        SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?", id);
        Genre genre = new Genre();

        if (filmRowsGenres.next()) {
            genre.setId(filmRowsGenres.getInt("GENRE_ID"));
            genre.setName(filmRowsGenres.getString("GENRE"));
        }
        return genre;
    }

    private void deleteFilm(Film film) {
        filmValidation.filmsValidation(film);
        String deleteAllLikeFilm = "DELETE FROM FILM_LIKES WHERE FILM_ID = ?";
        String deleteAllGenreFilm = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        String deleteAllMpaFilm = "DELETE FROM FILM_MPA WHERE FILM_ID = ?";
        String deleteFilm = "DELETE FROM FILMS WHERE FILM_ID = ?";

        jdbcTemplate.update(deleteAllLikeFilm, film.getId());
        jdbcTemplate.update(deleteAllGenreFilm, film.getId());
        jdbcTemplate.update(deleteAllMpaFilm, film.getId());
        jdbcTemplate.update(deleteFilm, film.getId());
    }

    private void selectAllFromFilms(Film film, SqlRowSet filmRowsFilms) {
        film.setId(filmRowsFilms.getInt("FILM_ID"));
        film.setName(Objects.requireNonNull(filmRowsFilms.getString("FILM_NAME")));
        film.setDescription(Objects.requireNonNull(filmRowsFilms.getString("DESCRIPTION")));
        film.setReleaseDate(Objects.requireNonNull(filmRowsFilms.getDate("RELEASEDATE")).toLocalDate());
        film.setDuration(filmRowsFilms.getInt("DURATION"));
        film.setRate(filmRowsFilms.getInt("RATE"));
    }

    private List<Genre> getGenre(SqlRowSet filmRowsGenre) {
        List<Genre> genres = new ArrayList<>();
        while (filmRowsGenre.next()) {
            Genre genre = new Genre();
            genre.setId(filmRowsGenre.getInt("GENRE_ID"));
            genres.add(genre);
        }
        return genres;
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
            usersId.add(filmRowsLiked.getInt("FILM_ID"));
        }
        film.setUsersId(usersId);

        film.setGenres(getGenre(filmRowsGenre));

        for (int i = 0; i < film.getGenres().size(); i++) {
            SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?",
                    film.getGenres().get(i).getId());

            if (filmRowsGenres.next()) {
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

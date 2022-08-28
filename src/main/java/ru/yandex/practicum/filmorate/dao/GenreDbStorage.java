package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final GenreRowMapper genreRowMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public List<Genre> getGenres() {
        SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES");
        List<Genre> genres = new ArrayList<>();

        while (filmRowsGenres.next()) {
            Genre genre = new Genre();
            genre.setId(filmRowsGenres.getInt("GENRE_ID"));
            genre.setName(filmRowsGenres.getString("GENRE_NAME"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        if (id < 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        SqlRowSet filmRowsGenres = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?", id);
        Genre genre = new Genre();

        if (filmRowsGenres.next()) {
            genre.setId(filmRowsGenres.getInt("GENRE_ID"));
            genre.setName(filmRowsGenres.getString("GENRE_NAME"));
        }
        return genre;
    }

    public List<Genre> getGenresByFilmId(int id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID IN " +
                "(SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sql, genreRowMapper, id);
    }
}

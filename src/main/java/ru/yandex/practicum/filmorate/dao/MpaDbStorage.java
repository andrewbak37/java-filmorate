package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getMPA() {
        SqlRowSet filmRowsRates = jdbcTemplate.queryForRowSet("SELECT * FROM MPA");
        List<MPA> genres = new ArrayList<>();

        while (filmRowsRates.next()) {
            MPA MPA = new MPA();
            MPA.setId(filmRowsRates.getInt("MPA_ID"));
            MPA.setName(filmRowsRates.getString("MPA_NAME"));
            genres.add(MPA);
        }
        return genres;
    }

    @Override
    public MPA getMpaById(int id) {
        if (id <= 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        SqlRowSet filmRowsRate = jdbcTemplate.queryForRowSet("SELECT * FROM MPA WHERE MPA_ID = ?", id);
        MPA mpa = new MPA();

        if (filmRowsRate.next()) {
            mpa.setId(filmRowsRate.getInt("MPA_ID"));
            mpa.setName(filmRowsRate.getString("MPA_NAME"));
        }
        return mpa;
    }
}

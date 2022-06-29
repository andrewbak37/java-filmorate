package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @Test
    void filmsValidation() {
        final FilmController filmController = new FilmController();
        final Film film = new Film();
        film.setName("");
        film.setDescription("Spring Framework (или коротко Spring) — универсальный фреймворк с открытым исходным кодом для Java-платформы. " +
                "Также существует форк для платформы .NET Framework, названный Spring.NET[2].\n" +
                "Первая версия была написана Родом Джонсоном, который впервые опубликовал её вместе с изданием своей книги " +
                "«Expert One-on-One Java EE Design and Development»[3] (Wrox Press, октябрь 2002 года).");
        film.setReleaseDate(LocalDate.of(1703, 9, 25));
        film.setDuration(-1);
        filmController.filmsValidation(film);
        assertEquals(film.getName(), ("name"));
        assertEquals(film.getDescription(), ("description"));
        assertEquals(film.getReleaseDate(), (LocalDate.of(2018, 9, 25)));
        assertEquals(film.getDuration(), (113));
    }
}
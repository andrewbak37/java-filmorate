package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private long duration;
}

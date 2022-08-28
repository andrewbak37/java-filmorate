package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    private String description;
    private @NonNull LocalDate releaseDate;
    @Positive
    private Integer duration;
    private @NonNull Integer rate;
    private MPA mpa;
    private List<Genre> genres;
    private Set<Integer> userLikes = new HashSet<>();
    private Set<Integer> usersId = new HashSet<>();

}

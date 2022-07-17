package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private Set<Integer> friends = new HashSet<>();
    @NotBlank
    private String login;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
    private LocalDate birthday;
}

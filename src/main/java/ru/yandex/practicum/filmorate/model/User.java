package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class User extends ObjectModel {
    @NotBlank
    private String login;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
    private LocalDate birthday;
}

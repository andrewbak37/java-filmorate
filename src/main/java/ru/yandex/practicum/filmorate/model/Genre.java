package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id") // Объект определяется только по полю id
@ToString
public class Genre {
    private Integer id;
    private String name;


}
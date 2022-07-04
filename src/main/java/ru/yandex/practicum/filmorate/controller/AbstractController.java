package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ObjectModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public abstract class AbstractController<T extends ObjectModel> {
    public int id = 0;
    private final Map<Integer, T> map = new HashMap<>();

    public int getNewId() {
        return ++id;
    }

    public T create(T model) {
        model.setId(getNewId());
        map.put(model.getId(), model);
        log.info("Объект добавлен");
        return model;
    }

    public Collection<T> findAll() {
        return map.values();
    }

    public T update(T model) {
        if (map.containsKey(model.getId())) {
            map.put(model.getId(), model);
            return model;
        } else {
            log.error("Ключа нет");
            throw new ValidationException();
        }
    }
}

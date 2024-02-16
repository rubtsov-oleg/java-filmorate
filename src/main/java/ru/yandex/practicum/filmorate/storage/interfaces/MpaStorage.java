package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa getById(Integer mpaId);

    List<Mpa> getAll();

    Mpa create(Mpa mpa);
}

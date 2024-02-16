package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getById(Integer genreId);

    List<Genre> getAll();

    Genre create(Genre genre);
}

package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film create(Film film);

    public Film update(Film film);

    public void delete(Integer filmId);

    public Film getById(Integer filmId);

    public List<Film> getAll();
}

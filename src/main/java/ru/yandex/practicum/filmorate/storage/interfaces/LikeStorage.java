package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface LikeStorage {
    Like create(Like like);

    void delete(Integer likeId);

    List<Like> getByFilm(Film film);

    Optional<Like> getByFilmAndUser(Film film, User user);
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        if (likeStorage.getByFilmAndUser(film, user).isPresent()) {
            throw new AlreadyExistsException("User with ID " + userId + " already liked this film");
        }
        Like like = new Like();
        like.setFilmId(film);
        like.setUserId(user);
        likeStorage.create(like);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        Optional<Like> like = likeStorage.getByFilmAndUser(film, user);
        if (like.isEmpty()) {
            throw new NoSuchElementException("User with ID " + userId + " not liked this film");
        }
        likeStorage.delete(like.get().getId());
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getAll();
        films.sort(Comparator.comparingInt((Film film) -> likeStorage.getByFilm(film).size()));
        films = films.subList(Math.max(films.size() - count, 0), films.size());
        Collections.reverse(films);
        return films;
    }

    public Film getById(Integer filmId) {
        return filmStorage.getById(filmId);
    }
}

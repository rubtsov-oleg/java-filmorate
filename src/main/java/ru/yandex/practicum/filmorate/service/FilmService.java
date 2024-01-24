package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        userStorage.getById(userId);
        Set<Integer> filmLikes = film.getLikes();
        if (filmLikes.contains(userId)) {
            throw new AlreadyExistsException("User with ID " + userId + " already liked this film");
        }
        filmLikes.add(userId);
        film.setLikes(filmLikes);
        filmStorage.update(film);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        Set<Integer> filmLikes = film.getLikes();
        if (!filmLikes.contains(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " not liked this film");
        }
        filmLikes.remove(userId);
        film.setLikes(filmLikes);
        filmStorage.update(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getAll();
        films.sort(Comparator.comparingInt((Film film) -> film.getLikes().size()));
        films = films.subList(Math.max(films.size() - count, 0), films.size());
        Collections.reverse(films);
        return films;
    }

    public Film getById(Integer filmId) {
        return filmStorage.getById(filmId);
    }
}

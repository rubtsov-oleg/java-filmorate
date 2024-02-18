package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.interfaces.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        Film enrichtedFilm = enrichmentMpaAndGenres(film);
        return filmStorage.create(enrichtedFilm);
    }

    public Film update(Film film) {
        Film enrichtedFilm = enrichmentMpaAndGenres(film);
        return filmStorage.update(enrichtedFilm);
    }

    public Film enrichmentMpaAndGenres(Film film) {
        Mpa mpa = film.getMpa();
        if (mpa != null) {
            Mpa existedMpa = mpaStorage.getById(mpa.getId());
            film.setMpa(existedMpa);
        }

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            Set<Genre> existedGenres = new HashSet<>();
            for (Genre genre : genres) {
                Genre existedGenre = genreStorage.getById(genre.getId());
                existedGenres.add(existedGenre);
            }
            List<Genre> sortedGenres = new ArrayList<>(existedGenres);
            sortedGenres.sort(Comparator.comparingInt(Genre::getId));
            film.setGenres(new LinkedHashSet<>(sortedGenres));
        }
        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        if (likeStorage.getByFilmAndUser(film, user).isPresent()) {
            throw new AlreadyExistsException("User with ID " + userId + " already liked this film");
        }
        Like like = new Like();
        like.setFilmId(film.getId());
        like.setUserId(user.getId());
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

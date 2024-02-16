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
        Mpa mpa1 = new Mpa();
        mpa1.setName("mpa1");
        mpaStorage.create(mpa1);

        Mpa mpa2 = new Mpa();
        mpa2.setName("mpa2");
        mpaStorage.create(mpa2);

        Mpa mpa3 = new Mpa();
        mpa3.setName("mpa3");
        mpaStorage.create(mpa3);

        Genre genre1 = new Genre();
        genre1.setName("genre1");
        genreStorage.create(genre1);


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

        List<Genre> genres = film.getGenres();
        if (genres != null) {
            List<Genre> existedGenres = new ArrayList<>();
            for (Genre genre: genres) {
                Genre existedGenre = genreStorage.getById(genre.getId());
                existedGenres.add(existedGenre);
            }
            film.setGenres(existedGenres);
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

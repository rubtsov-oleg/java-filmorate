package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    public Film create(Film film){
        film.setId(IdIterator.getFilmId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        Integer filmId = film.getId();
        if (!films.containsKey(filmId)) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }
        films.put(filmId, film);
        return film;
    }

    public void delete(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }
        films.remove(filmId);
    }

    public Film getById(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }
        return films.get(filmId);
    }

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(IdIterator.getFilmId());
        log.info("FILM CREATE INPUT - {}!", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("FILM UPDATE INPUT - {}!", film);
        Integer filmId = film.getId();
        if (!films.containsKey(filmId)) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }
        films.put(filmId, film);
        return film;
    }
}


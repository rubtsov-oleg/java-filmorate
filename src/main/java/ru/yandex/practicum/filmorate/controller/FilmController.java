package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("FILM CREATE INPUT - {}!", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("FILM UPDATE INPUT - {}!", film);
        return filmService.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilms(
            @Valid
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Count should be bigger than 0")
            Integer count
    ) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable Integer filmId) {
        return filmService.getById(filmId);
    }
}


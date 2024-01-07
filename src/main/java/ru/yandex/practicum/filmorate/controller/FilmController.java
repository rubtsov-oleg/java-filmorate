package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<?> update(@Valid @RequestBody Film film) {
        log.info("FILM UPDATE INPUT - {}!", film);
        Integer filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            return ResponseEntity.ok(film);
        } else {
            log.info("FILM UPDATE ERROR - Film with ID {} not found!", filmId);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Film with ID " + filmId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.info("VALIDATION ERRORS - {}!", errors);
        return errors;
    }
}


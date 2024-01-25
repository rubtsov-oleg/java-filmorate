package ru.yandex.practicum.filmorate.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

public class FilmValidationTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testFilmSuccess() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDuration(2);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testFilmFailName() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDuration(2);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }

    @Test
    public void testFilmFailDescription() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("q".repeat(201));
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDuration(2);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }

    @Test
    public void testFilmFailDate() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));
        film.setDuration(2);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }

    @Test
    public void testFilmFailDuration() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.now().minusYears(5));
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }
}
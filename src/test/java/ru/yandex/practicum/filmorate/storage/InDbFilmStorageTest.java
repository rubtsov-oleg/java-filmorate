package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InDbFilmStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private InDbFilmStorage inDbFilmStorage;
    private Mpa mpa1;
    private Mpa mpa2;
    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    public void setUp() {
        DatabaseUtil databaseUtil = new DatabaseUtil(jdbcTemplate);
        inDbFilmStorage = new InDbFilmStorage(jdbcTemplate, databaseUtil);

        InDbMpaStorage inDbMpaStorage = new InDbMpaStorage(jdbcTemplate);
        mpa1 = inDbMpaStorage.getById(1);
        mpa2 = inDbMpaStorage.getById(2);

        InDbGenreStorage inDbGenreStorage = new InDbGenreStorage(jdbcTemplate);
        genre1 = inDbGenreStorage.getById(1);
        genre2 = inDbGenreStorage.getById(2);
    }

    @Test
    public void testCreate() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.of(2020, 5, 3));
        film.setMpa(mpa1);
        film.setGenres(new HashSet<>());
        inDbFilmStorage.create(film);

        Film savedFilm = inDbFilmStorage.getById(1);

        assertNotNull(savedFilm);
        assertEquals(savedFilm.getName(), film.getName());
        assertEquals(savedFilm.getDescription(), film.getDescription());
        assertEquals(savedFilm.getDuration(), film.getDuration());
        assertEquals(savedFilm.getReleaseDate(), film.getReleaseDate());
        assertEquals(savedFilm.getMpa(), film.getMpa());
        assertEquals(savedFilm.getGenres(), film.getGenres());
    }

    @Test
    public void testUpdate() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.of(2020, 5, 3));
        film.setMpa(mpa1);
        film.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));
        Film savedFilm = inDbFilmStorage.create(film);

        assertEquals(savedFilm.getGenres().size(), 2);

        savedFilm.setGenres(new HashSet<>(Collections.singletonList(genre2)));
        savedFilm.setMpa(mpa2);
        savedFilm.setDuration(5);
        inDbFilmStorage.update(savedFilm);
        Film savedFilmAfterChange = inDbFilmStorage.getById(1);

        assertEquals(savedFilmAfterChange.getGenres(), new HashSet<>(Collections.singletonList(genre2)));
        assertEquals(savedFilmAfterChange.getMpa(), mpa2);
        assertEquals(savedFilmAfterChange.getDuration(), 5);

        Film film2 = new Film();
        film2.setName("Test");
        film2.setDescription("Test");
        film2.setDuration(1);
        film2.setReleaseDate(LocalDate.of(2020, 5, 3));
        film2.setMpa(mpa1);
        film2.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));

        assertThrows(NoSuchElementException.class, () -> {
            inDbFilmStorage.update(film2);
        });
    }

    @Test
    public void testDelete() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.of(2020, 5, 3));
        film.setMpa(mpa1);
        film.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));
        inDbFilmStorage.create(film);
        Film savedFilm = inDbFilmStorage.getById(1);

        inDbFilmStorage.delete(savedFilm.getId());

        assertThrows(NoSuchElementException.class, () -> {
            inDbFilmStorage.getById(1);
        });
        assertThrows(NoSuchElementException.class, () -> {
            inDbFilmStorage.delete(2);
        });
    }

    @Test
    public void getByIdTest() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.of(2020, 5, 3));
        film.setMpa(mpa1);
        film.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));
        inDbFilmStorage.create(film);

        Film savedFilm = inDbFilmStorage.getById(1);

        assertEquals(savedFilm.getName(), film.getName());
        assertEquals(savedFilm.getDescription(), film.getDescription());
        assertThrows(NoSuchElementException.class, () -> {
            inDbFilmStorage.getById(2);
        });
    }

    @Test
    public void getAllTest() {
        assertEquals(inDbFilmStorage.getAll(), new ArrayList<>());

        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.of(2020, 5, 3));
        film.setMpa(mpa1);
        film.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));
        inDbFilmStorage.create(film);

        assertEquals(inDbFilmStorage.getAll().size(), 1);

        Film film2 = new Film();
        film2.setName("Test");
        film2.setDescription("Test");
        film2.setDuration(1);
        film2.setReleaseDate(LocalDate.of(2020, 5, 3));
        film2.setMpa(mpa1);
        film2.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));
        inDbFilmStorage.create(film2);

        assertEquals(inDbFilmStorage.getAll().size(), 2);
        assertEquals(inDbFilmStorage.getAll().get(0), film);
        assertEquals(inDbFilmStorage.getAll().get(1), film2);
    }
}

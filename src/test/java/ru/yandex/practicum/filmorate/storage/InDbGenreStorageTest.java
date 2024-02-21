package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InDbGenreStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private InDbGenreStorage inDbGenreStorage;

    @BeforeEach
    public void setUp() {
        inDbGenreStorage = new InDbGenreStorage(jdbcTemplate);
    }

    @Test
    public void testGetById() {
        Genre genre = inDbGenreStorage.getById(1);

        assertThrows(NoSuchElementException.class, () -> {
            inDbGenreStorage.getById(999);
        });
        assertNotNull(genre);
        assertEquals(genre.getName(), "Комедия");
    }

    @Test
    public void testGetAll() {
        List<Genre> genreList = inDbGenreStorage.getAll();

        assertEquals(genreList.size(), 6);
        assertEquals(genreList.get(0).getName(), "Комедия");
    }

    @Test
    public void testGetByIds() {
        List<Genre> genreList = inDbGenreStorage.getByIds(Arrays.asList(1, 2));

        assertEquals(genreList.size(), 2);
        assertEquals(genreList.get(0).getName(), "Комедия");
        assertEquals(genreList.get(1).getName(), "Драма");
    }
}

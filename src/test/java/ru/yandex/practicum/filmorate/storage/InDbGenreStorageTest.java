package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.indb.*;

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
        DatabaseUtil databaseUtil = new DatabaseUtil(jdbcTemplate);
        inDbGenreStorage = new InDbGenreStorage(jdbcTemplate, databaseUtil);
    }

    @Test
    public void testCreate() {
        Genre newGenre = new Genre();
        newGenre.setName("Test");
        inDbGenreStorage.create(newGenre);

        Genre savedGenre = inDbGenreStorage.getById(1);

        assertNotNull(savedGenre);
        assertEquals(newGenre.getName(), savedGenre.getName());
    }

    @Test
    public void testGetById() {
        Genre newGenre = new Genre();
        newGenre.setName("Test");
        inDbGenreStorage.create(newGenre);

        assertThrows(NoSuchElementException.class, () -> {
            inDbGenreStorage.getById(2);
        });
        assertNotNull(inDbGenreStorage.getById(1));
    }

    @Test
    public void testGetAll() {
        Genre newGenre = new Genre();
        newGenre.setName("Test");
        Genre newGenre2 = new Genre();
        newGenre2.setName("Test2");

        List<Genre> emptyList = inDbGenreStorage.getAll();
        inDbGenreStorage.create(newGenre);
        List<Genre> listWithOneItem = inDbGenreStorage.getAll();
        inDbGenreStorage.create(newGenre2);
        List<Genre> listWithTwoItems = inDbGenreStorage.getAll();

        assertEquals(emptyList.size(), 0);
        assertEquals(listWithOneItem.size(), 1);
        assertEquals(listWithTwoItems.size(), 2);
    }
}

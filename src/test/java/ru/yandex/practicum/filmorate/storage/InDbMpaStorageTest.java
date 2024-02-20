package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;


import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.indb.DatabaseUtil;
import ru.yandex.practicum.filmorate.storage.indb.InDbMpaStorage;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InDbMpaStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private InDbMpaStorage inDbMpaStorage;

    @BeforeEach
    public void setUp() {
        DatabaseUtil databaseUtil = new DatabaseUtil(jdbcTemplate);
        inDbMpaStorage = new InDbMpaStorage(jdbcTemplate, databaseUtil);
    }

    @Test
    public void testCreate() {
        Mpa newMpa = new Mpa();
        newMpa.setName("Test");
        inDbMpaStorage.create(newMpa);

        Mpa savedMpa = inDbMpaStorage.getById(1);

        assertNotNull(savedMpa);
        assertEquals(newMpa.getName(), savedMpa.getName());
    }

    @Test
    public void testGetById() {
        Mpa newMpa = new Mpa();
        newMpa.setName("Test");
        inDbMpaStorage.create(newMpa);

        assertThrows(NoSuchElementException.class, () -> {
            inDbMpaStorage.getById(2);
        });
        assertNotNull(inDbMpaStorage.getById(1));
    }

    @Test
    public void testGetAll() {
        Mpa newMpa = new Mpa();
        newMpa.setName("Test");
        Mpa newMpa2 = new Mpa();
        newMpa2.setName("Test2");

        List<Mpa> emptyList = inDbMpaStorage.getAll();
        inDbMpaStorage.create(newMpa);
        List<Mpa> listWithOneItem = inDbMpaStorage.getAll();
        inDbMpaStorage.create(newMpa2);
        List<Mpa> listWithTwoItems = inDbMpaStorage.getAll();

        assertEquals(emptyList.size(), 0);
        assertEquals(listWithOneItem.size(), 1);
        assertEquals(listWithTwoItems.size(), 2);
    }
}

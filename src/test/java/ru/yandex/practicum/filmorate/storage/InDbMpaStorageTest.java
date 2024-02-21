package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;


import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        inDbMpaStorage = new InDbMpaStorage(jdbcTemplate);
    }

    @Test
    public void testGetById() {
        Mpa mpa = inDbMpaStorage.getById(1);

        assertThrows(NoSuchElementException.class, () -> {
            inDbMpaStorage.getById(999);
        });
        assertNotNull(mpa);
        assertEquals(mpa.getName(), "G");
    }

    @Test
    public void testGetAll() {
        List<Mpa> mpaList = inDbMpaStorage.getAll();

        assertEquals(mpaList.size(), 5);
        assertEquals(mpaList.get(0).getName(), "G");
    }
}

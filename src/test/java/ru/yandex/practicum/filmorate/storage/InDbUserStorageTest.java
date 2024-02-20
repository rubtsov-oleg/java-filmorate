package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.indb.DatabaseUtil;
import ru.yandex.practicum.filmorate.storage.indb.InDbUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InDbUserStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private InDbUserStorage inDbUserStorage;

    @BeforeEach
    public void setUp() {
        DatabaseUtil databaseUtil = new DatabaseUtil(jdbcTemplate);
        inDbUserStorage = new InDbUserStorage(jdbcTemplate, databaseUtil);
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        inDbUserStorage.create(user);

        User savedUser = inDbUserStorage.getById(1);

        assertNotNull(savedUser);
        assertEquals(savedUser.getName(), user.getName());
        assertEquals(savedUser.getLogin(), user.getLogin());
        assertEquals(savedUser.getBirthday(), user.getBirthday());
        assertEquals(savedUser.getEmail(), user.getEmail());
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        User savedUser = inDbUserStorage.create(user);

        savedUser.setName("TEST2");
        savedUser.setEmail("test2@mail.ru");
        inDbUserStorage.update(savedUser);
        User savedUserAfterChange = inDbUserStorage.getById(1);

        assertEquals(savedUserAfterChange.getName(), "TEST2");
        assertEquals(savedUserAfterChange.getEmail(), "test2@mail.ru");

        User user2 = new User();
        user2.setName("Test");
        user2.setLogin("Test");
        user2.setBirthday(LocalDate.of(2020, 5, 3));
        user2.setEmail("test@mail.ru");

        assertThrows(NoSuchElementException.class, () -> {
            inDbUserStorage.update(user2);
        });
    }

    @Test
    public void testDelete() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        User savedUser = inDbUserStorage.create(user);

        inDbUserStorage.delete(savedUser.getId());

        assertThrows(NoSuchElementException.class, () -> {
            inDbUserStorage.getById(1);
        });
        assertThrows(NoSuchElementException.class, () -> {
            inDbUserStorage.delete(2);
        });
    }

    @Test
    public void getByIdTest() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        inDbUserStorage.create(user);

        User savedUser = inDbUserStorage.getById(1);

        assertEquals(savedUser.getName(), user.getName());
        assertEquals(savedUser.getEmail(), user.getEmail());
        assertThrows(NoSuchElementException.class, () -> {
            inDbUserStorage.getById(2);
        });
    }

    @Test
    public void getAllTest() {
        assertEquals(inDbUserStorage.getAll(), new ArrayList<>());

        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        inDbUserStorage.create(user);

        assertEquals(inDbUserStorage.getAll().size(), 1);

        User user2 = new User();
        user2.setName("Test");
        user2.setLogin("Test");
        user2.setBirthday(LocalDate.of(2020, 5, 3));
        user2.setEmail("test@mail.ru");
        inDbUserStorage.create(user2);

        assertEquals(inDbUserStorage.getAll().size(), 2);
        assertEquals(inDbUserStorage.getAll().get(0), user);
        assertEquals(inDbUserStorage.getAll().get(1), user2);
    }
}

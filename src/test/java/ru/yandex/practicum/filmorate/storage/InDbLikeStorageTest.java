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

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InDbLikeStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private InDbLikeStorage inDbLikeStorage;
    private User user;
    private User user2;
    private Film film;
    private Film film2;

    @BeforeEach
    public void setUp() {
        DatabaseUtil databaseUtil = new DatabaseUtil(jdbcTemplate);
        inDbLikeStorage = new InDbLikeStorage(jdbcTemplate, databaseUtil);
        InDbFilmStorage inDbFilmStorage = new InDbFilmStorage(jdbcTemplate, databaseUtil);
        InDbUserStorage inDbUserStorage = new InDbUserStorage(jdbcTemplate, databaseUtil);
        InDbMpaStorage inDbMpaStorage = new InDbMpaStorage(jdbcTemplate);
        user = createUser(inDbUserStorage);
        user2 = createUser(inDbUserStorage);
        film = createFilm(inDbFilmStorage, inDbMpaStorage);
        film2 = createFilm(inDbFilmStorage, inDbMpaStorage);
    }

    @Test
    public void testCreate() {
        Like newLike = new Like();
        newLike.setUserId(user.getId());
        newLike.setFilmId(film.getId());
        inDbLikeStorage.create(newLike);

        Optional<Like> savedLike = inDbLikeStorage.getByFilmAndUser(film, user);

        assertTrue(savedLike.isPresent());
        assertEquals(savedLike.get().getFilmId(), film.getId());
        assertEquals(savedLike.get().getUserId(), user.getId());
    }

    @Test
    public void testDelete() {
        Like newLike = new Like();
        newLike.setUserId(user.getId());
        newLike.setFilmId(film.getId());
        inDbLikeStorage.create(newLike);
        Optional<Like> savedLike = inDbLikeStorage.getByFilmAndUser(film, user);

        inDbLikeStorage.delete(savedLike.get().getId());

        assertTrue(inDbLikeStorage.getByFilmAndUser(film, user).isEmpty());
        assertThrows(NoSuchElementException.class, () -> {
            inDbLikeStorage.delete(2);
        });
    }

    @Test
    public void testGetByFilm() {
        List<Like> emptySavedLikes = inDbLikeStorage.getByFilm(film);

        Like newLike = new Like();
        newLike.setUserId(user.getId());
        newLike.setFilmId(film.getId());
        inDbLikeStorage.create(newLike);

        List<Like> savedLikesWithOneItem = inDbLikeStorage.getByFilm(film);

        Like newLike2 = new Like();
        newLike2.setUserId(user2.getId());
        newLike2.setFilmId(film.getId());
        inDbLikeStorage.create(newLike2);

        List<Like> savedLikesWithTwoItems = inDbLikeStorage.getByFilm(film);

        assertEquals(emptySavedLikes.size(), 0);
        assertEquals(savedLikesWithOneItem.size(), 1);
        assertEquals(savedLikesWithTwoItems.size(), 2);
    }

    @Test
    public void testGetByFilmAndUser() {
        Like newLike = new Like();
        newLike.setUserId(user.getId());
        newLike.setFilmId(film.getId());
        inDbLikeStorage.create(newLike);

        Optional<Like> savedLike = inDbLikeStorage.getByFilmAndUser(film, user);
        Optional<Like> savedLike2 = inDbLikeStorage.getByFilmAndUser(film2, user);

        assertTrue(savedLike.isPresent());
        assertTrue(savedLike2.isEmpty());
        assertEquals(savedLike.get().getFilmId(), film.getId());
        assertEquals(savedLike.get().getUserId(), user.getId());
    }

    public User createUser(InDbUserStorage inDbUserStorage) {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        return inDbUserStorage.create(user);
    }

    public Film createFilm(InDbFilmStorage inDbFilmStorage, InDbMpaStorage inDbMpaStorage) {
        Mpa mpa = new Mpa();
        mpa.setName("test");
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.of(2020, 5, 3));
        film.setMpa(inDbMpaStorage.getById(1));
        film.setGenres(new HashSet<>());
        return inDbFilmStorage.create(film);
    }
}

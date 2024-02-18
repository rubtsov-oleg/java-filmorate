package ru.yandex.practicum.filmorate.storage.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.indb.statements.FilmPreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.Date;
import java.util.*;

@Component
@Profile("inDb")
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseUtil databaseUtil;

    private final RowMapper<Film> filmRowMapper = (resultSet, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));

        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(mpa);
        return film;
    };

    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    };

    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?);";
        int filmId = databaseUtil.insertAndReturnId(sql, new FilmPreparedStatementSetter(film));
        film.setId(filmId);

        if (!film.getGenres().isEmpty()) {
            insertFilmGenres(filmId, film.getGenres());
        }

        return film;
    }

    private void insertFilmGenres(Integer filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);";
        genres.forEach(genre -> jdbcTemplate.update(sql, filmId, genre.getId()));
    }

    public Film update(Film film) {
        Integer filmId = film.getId();
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?";
        int rowsAffected = jdbcTemplate.update(
                sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), filmId
        );
        if (rowsAffected == 0) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }

        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        if (!film.getGenres().isEmpty()) {
            insertFilmGenres(filmId, film.getGenres());
        }

        return film;
    }

    public void delete(Integer filmId) {
        String deleteGenresSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, filmId);

        String deleteFilmSql = "DELETE FROM films where id = ?";
        int rowsAffected = jdbcTemplate.update(deleteFilmSql, filmId);
        if (rowsAffected == 0) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }
    }

    public Film getById(Integer filmId) {
        String sql = "SELECT " +
                "  f.id, " +
                "  f.name," +
                "  f.description," +
                "  f.release_date," +
                "  f.duration," +
                "  m.id as mpa_id, " +
                "  m.name as mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, filmId);
            if (film != null) {
                Set<Genre> genres = getGenresByFilm(film.getId());
                film.setGenres(genres);
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Film with ID " + filmId + " not found");
        }
    }

    public Set<Genre> getGenresByFilm(Integer filmId) {
        String sql = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(sql, genreRowMapper, filmId));
    }

    public List<Film> getAll() {
        String sql = "SELECT " +
                "  f.id, " +
                "  f.name," +
                "  f.description," +
                "  f.release_date," +
                "  f.duration," +
                "  m.id as mpa_id, " +
                "  m.name as mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.id ";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        for (Film film : films) {
            Set<Genre> genres = getGenresByFilm(film.getId());
            film.setGenres(genres);
        }
        return films;
    }
}

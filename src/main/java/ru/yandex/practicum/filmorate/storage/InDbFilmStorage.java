package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.statements.FilmPreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
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
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("genre_name"));
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
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);",
                genres,
                genres.size(),
                (PreparedStatement ps, Genre genre) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                });
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
        String sql = "SELECT g.id as genre_id, g.name as genre_name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(sql, genreRowMapper, filmId));
    }

    public List<Film> getAll() {
        String sql = "SELECT " +
                "  f.*, " +
                "  m.id as mpa_id, " +
                "  m.name as mpa_name, " +
                "  g.id as genre_id, " +
                "  g.name as genre_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id";

        Map<Integer, Film> filmMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            int filmId = resultSet.getInt("id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = filmRowMapper.mapRow(resultSet, resultSet.getRow());
                if (film != null) {
                    film.setGenres(new HashSet<>());
                    filmMap.put(filmId, film);
                }

            }
            int genreId = resultSet.getInt("genre_id");
            if (genreId != 0) {
                Genre genre = genreRowMapper.mapRow(resultSet, resultSet.getRow());
                if (film != null) {
                    film.getGenres().add(genre);
                }
            }
        });
        return new ArrayList<>(filmMap.values());
    }
}

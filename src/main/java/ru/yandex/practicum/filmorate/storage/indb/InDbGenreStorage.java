package ru.yandex.practicum.filmorate.storage.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.indb.statements.GenrePreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@Profile("inDb")
@RequiredArgsConstructor
public class InDbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseUtil databaseUtil;

    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    };

    public Genre create(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (?);";
        int genreId = databaseUtil.insertAndReturnId(sql, new GenrePreparedStatementSetter(genre));
        genre.setId(genreId);
        return genre;
    }

    public Genre getById(Integer genreId) {
        String sql = "SELECT id, name FROM genres WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, genreRowMapper, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Genre with ID " + genreId + " not found");
        }
    }

    public List<Genre> getAll() {
        String sql = "SELECT id, name FROM genres;";
        return jdbcTemplate.query(sql, genreRowMapper);
    }
}

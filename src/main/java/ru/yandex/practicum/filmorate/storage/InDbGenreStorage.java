package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class InDbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    };

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

    public List<Genre> getByIds(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        String sql = "SELECT id, name FROM genres WHERE id IN (" + inSql + ");";

        return jdbcTemplate.query(sql, genreRowMapper, genreIds.toArray());
    }
}

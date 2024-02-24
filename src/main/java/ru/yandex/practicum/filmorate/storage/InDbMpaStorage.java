package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class InDbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mpa> mpaRowMapper = (resultSet, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    };

    public Mpa getById(Integer mpaId) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, mpaRowMapper, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("MPA with ID " + mpaId + " not found");
        }
    }

    public List<Mpa> getAll() {
        String sql = "SELECT id, name FROM mpa;";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }
}

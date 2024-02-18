package ru.yandex.practicum.filmorate.storage.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.indb.statements.MpaPreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@Profile("inDb")
@RequiredArgsConstructor
public class InDbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseUtil databaseUtil;

    private final RowMapper<Mpa> mpaRowMapper = (resultSet, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    };

    public Mpa create(Mpa mpa) {
        String sql = "INSERT INTO mpa (name) VALUES (?);";
        int mpaId = databaseUtil.insertAndReturnId(sql, new MpaPreparedStatementSetter(mpa));
        mpa.setId(mpaId);
        return mpa;
    }

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

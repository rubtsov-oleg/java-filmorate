package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.statements.UserPreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class InDbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseUtil databaseUtil;

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return user;
    };

    public User getById(Integer userId) {
        String sql = "SELECT id, email, login, name, birthday  FROM users WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
    }

    public List<User> getAll() {
        String sql = "SELECT id, email, login, name, birthday FROM users;";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void delete(Integer userId) {
        String sql = "DELETE FROM users where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId);
        if (rowsAffected == 0) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
    }

    public User create(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?);";
        int userId = databaseUtil.insertAndReturnId(sql, new UserPreparedStatementSetter(user));
        user.setId(userId);
        return user;
    }

    public User update(User user) {
        Integer userId = user.getId();
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? where id = ?";
        int rowsAffected = jdbcTemplate.update(
                sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), userId
        );
        if (rowsAffected == 0) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
        return user;
    }
}

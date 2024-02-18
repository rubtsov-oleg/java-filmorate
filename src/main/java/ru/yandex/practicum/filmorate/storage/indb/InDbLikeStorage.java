package ru.yandex.practicum.filmorate.storage.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.indb.statements.LikePreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Profile("inDb")
@RequiredArgsConstructor
public class InDbLikeStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseUtil databaseUtil;

    private final RowMapper<Like> likeRowMapper = (resultSet, rowNum) -> {
        Like like = new Like();
        like.setId(resultSet.getInt("id"));
        like.setFilmId(resultSet.getInt("film_id"));
        like.setUserId(resultSet.getInt("user_id"));
        return like;
    };

    public Like create(Like like) {
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?);";
        int likeId = databaseUtil.insertAndReturnId(sql, new LikePreparedStatementSetter(like));
        like.setId(likeId);
        return like;
    }

    public void delete(Integer likeId) {
        String sql = "DELETE FROM likes where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, likeId);
        if (rowsAffected == 0) {
            throw new NoSuchElementException("Like with ID " + likeId + " not found");
        }
    }

    public List<Like> getByFilm(Film film) {
        String sql = "SELECT * FROM likes WHERE film_id = ?;";
        return jdbcTemplate.query(sql, likeRowMapper, film.getId());
    }

    public Optional<Like> getByFilmAndUser(Film film, User user) {
        String sql = "SELECT * FROM likes WHERE film_id = ? and user_id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, likeRowMapper, film.getId(), user.getId()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

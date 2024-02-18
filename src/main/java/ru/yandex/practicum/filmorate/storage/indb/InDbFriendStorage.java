package ru.yandex.practicum.filmorate.storage.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.indb.statements.FriendPreparedStatementSetter;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Profile("inDb")
@RequiredArgsConstructor
public class InDbFriendStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseUtil databaseUtil;

    private final RowMapper<Friend> friendRowMapper = (resultSet, rowNum) -> {
        Friend friend = new Friend();
        friend.setId(resultSet.getInt("id"));
        friend.setUserId(resultSet.getInt("user_id"));
        friend.setFriendId(resultSet.getInt("friend_id"));
        friend.setFriendStatus(FriendStatus.valueOf(resultSet.getString("friend_status")));
        return friend;
    };

    public Friend create(Friend friendEntry) {
        String sql = "INSERT INTO friends (user_id, friend_id, friend_status) VALUES (?, ?, ?);";
        int friendId = databaseUtil.insertAndReturnId(sql, new FriendPreparedStatementSetter(friendEntry));
        friendEntry.setId(friendId);
        return friendEntry;
    }

    public Friend update(Friend friendEntry) {
        Integer friendEntryId = friendEntry.getId();
        String sql = "UPDATE friends SET friend_status = ? where id = ?";
        int rowsAffected = jdbcTemplate.update(
                sql, friendEntry.getFriendStatus().name(), friendEntry.getId()
        );
        if (rowsAffected == 0) {
            throw new NoSuchElementException("FriendEntry with ID " + friendEntryId + " not found");
        }
        return friendEntry;
    }

    public void delete(Integer friendEntryId) {
        String sql = "DELETE FROM friends where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, friendEntryId);
        if (rowsAffected == 0) {
            throw new NoSuchElementException("FriendEntry with ID " + friendEntryId + " not found");
        }
    }

    public List<Friend> getByUser(User user) {
        String sql = "SELECT * FROM friends f WHERE (f.user_id = ?) OR (f.friend_id = ? AND f.friend_status = 'CONFIRMED');";
        return jdbcTemplate.query(sql, friendRowMapper, user.getId(), user.getId());
    }

    public Optional<Friend> getByUserAndFriend(User user, User friend) {
        String sql = "SELECT * FROM friends WHERE user_id = ? and friend_id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, friendRowMapper, user.getId(), friend.getId()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

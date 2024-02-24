package ru.yandex.practicum.filmorate.storage.statements;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.interfaces.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class FriendPreparedStatementSetter implements PreparedStatementSetter {
    private final Friend friend;

    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setInt(1, friend.getUserId());
        ps.setInt(2, friend.getFriendId());
        ps.setString(3, friend.getFriendStatus().name());
    }
}

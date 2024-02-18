package ru.yandex.practicum.filmorate.storage.indb.statements;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.indb.interfaces.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class LikePreparedStatementSetter implements PreparedStatementSetter {
    private final Like like;

    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setInt(1, like.getUserId());
        ps.setInt(2, like.getFilmId());
    }
}
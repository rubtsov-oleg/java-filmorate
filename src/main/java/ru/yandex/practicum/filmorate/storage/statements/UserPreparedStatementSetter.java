package ru.yandex.practicum.filmorate.storage.statements;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.PreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class UserPreparedStatementSetter implements PreparedStatementSetter {
    private final User user;

    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getLogin());
        ps.setString(3, user.getName());
        ps.setDate(4, Date.valueOf(user.getBirthday()));
    }
}

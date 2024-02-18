package ru.yandex.practicum.filmorate.storage.indb.statements;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.indb.interfaces.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class MpaPreparedStatementSetter implements PreparedStatementSetter {
    private final Mpa mpa;

    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, mpa.getName());
    }
}
package ru.yandex.practicum.filmorate.storage.indb.statements;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.indb.interfaces.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class GenrePreparedStatementSetter implements PreparedStatementSetter {
    private final Genre genre;

    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, genre.getName());
    }
}
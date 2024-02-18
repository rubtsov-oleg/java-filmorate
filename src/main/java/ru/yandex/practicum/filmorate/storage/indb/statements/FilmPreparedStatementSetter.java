package ru.yandex.practicum.filmorate.storage.indb.statements;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.indb.interfaces.PreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class FilmPreparedStatementSetter implements PreparedStatementSetter {
    private final Film film;

    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, film.getName());
        ps.setString(2, film.getDescription());
        ps.setDate(3, Date.valueOf(film.getReleaseDate()));
        ps.setInt(4, film.getDuration());
        ps.setInt(5, film.getMpa().getId());
    }
}

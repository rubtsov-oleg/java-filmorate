package ru.yandex.practicum.filmorate.core;

public class IdIterator {
    public static int getFilmId() {
        filmId += 1;
        return filmId;
    }

    public static int getUserId() {
        userId += 1;
        return userId;
    }

    private static int filmId = 0;
    private static int userId = 0;
}

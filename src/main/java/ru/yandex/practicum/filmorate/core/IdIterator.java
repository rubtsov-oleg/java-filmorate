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

    public static int getLikeId() {
        likeId += 1;
        return likeId;
    }

    public static int getFriendId() {
        friendId += 1;
        return friendId;
    }

    public static int getGenreId() {
        genreId += 1;
        return genreId;
    }

    public static int getMpaId() {
        mpaId += 1;
        return mpaId;
    }

    private static int filmId = 0;
    private static int userId = 0;
    private static int likeId = 0;
    private static int friendId = 0;
    private static int genreId = 0;
    private static int mpaId = 0;
}

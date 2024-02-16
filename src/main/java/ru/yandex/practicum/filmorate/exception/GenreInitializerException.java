package ru.yandex.practicum.filmorate.exception;

public class GenreInitializerException extends RuntimeException {
    public GenreInitializerException(String message, Throwable cause) {
        super(message, cause);
    }
}
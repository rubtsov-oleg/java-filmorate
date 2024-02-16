package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class InMemoryGenreStorage implements GenreStorage {
    private final HashMap<Integer, Genre> genres = new HashMap<>();

    public Genre create(Genre genre) {
        genre.setId(IdIterator.getGenreId());
        genres.put(genre.getId(), genre);
        return genre;
    }
    public Genre getById(Integer genreId) {
        if (!genres.containsKey(genreId)) {
            throw new NoSuchElementException("Genre with ID " + genreId + " not found");
        }
        return genres.get(genreId);
    }

    public List<Genre> getAll() {
        return new ArrayList<>(genres.values());
    }
}

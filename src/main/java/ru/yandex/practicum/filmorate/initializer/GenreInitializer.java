package ru.yandex.practicum.filmorate.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreInitializerException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreInitializer {
    private final GenreStorage genreStorage;

    @Value("${genres.file.path}")
    private String genresFilePath;

    @PostConstruct
    public void init() {
        List<Genre> genres = genreStorage.getAll();
        List<String> genreNames = new ArrayList<>();
        for (Genre genre : genres) {
            genreNames.add(genre.getName());
        }
        try (FileReader reader = new FileReader(genresFilePath, StandardCharsets.UTF_8)) {
            BufferedReader br = new BufferedReader(reader);

            while (br.ready()) {
                String name = br.readLine();
                if (!genreNames.contains(name)) {
                    genreStorage.create(createGenre(name));
                }
            }
        } catch (IOException e) {
            throw new GenreInitializerException("Ошибка при чтении данных из файла", e);
        }
    }

    private Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return genre;
    }
}

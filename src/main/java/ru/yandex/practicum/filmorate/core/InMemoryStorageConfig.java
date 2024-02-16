package ru.yandex.practicum.filmorate.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.interfaces.*;

@Configuration
@Profile("inMemory")
public class InMemoryStorageConfig {

    @Bean
    public FilmStorage inMemoryFilmStorage() {
        return new InMemoryFilmStorage();
    }

    @Bean
    public FriendStorage inMemoryFriendStorage() {
        return new InMemoryFriendStorage();
    }


    @Bean
    public GenreStorage inMemoryGenreStorage() {
        return new InMemoryGenreStorage();
    }

    @Bean
    public LikeStorage inMemoryLikeStorage() {
        return new InMemoryLikeStorage();
    }

    @Bean
    public MpaStorage inMemoryMpaStorage() {
        return new InMemoryMpaStorage();
    }

    @Bean
    public UserStorage inMemoryUserStorage() {
        return new InMemoryUserStorage();
    }
}
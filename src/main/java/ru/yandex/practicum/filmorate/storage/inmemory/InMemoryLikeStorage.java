package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;

import java.util.*;


@Component
@Profile("inMemory")
public class InMemoryLikeStorage implements LikeStorage {
    private final HashMap<Integer, Like> likes = new HashMap<>();

    public Like create(Like like) {
        like.setId(IdIterator.getLikeId());
        likes.put(like.getId(), like);
        return like;
    }

    public void delete(Integer likeId) {
        if (!likes.containsKey(likeId)) {
            throw new NoSuchElementException("Like with ID " + likeId + " not found");
        }
        likes.remove(likeId);
    }

    public List<Like> getByFilm(Film film) {
        List<Like> filmLikes = new ArrayList<>();
        for (Like like : likes.values()) {
            if (Objects.equals(like.getFilmId(), film.getId())) {
                filmLikes.add(like);
            }
        }
        return filmLikes;
    }

    public Optional<Like> getByFilmAndUser(Film film, User user) {
        for (Like like : likes.values()) {
            if (Objects.equals(like.getFilmId(), film.getId()) && Objects.equals(like.getUserId(), user.getId())) {
                return Optional.of(like);
            }
        }
        return Optional.empty();
    }
}

package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    public User create(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(IdIterator.getUserId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        Integer userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
        users.put(userId, user);
        return user;
    }

    public void delete(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
        users.remove(userId);
    }

    public User getById(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
        return (User) users.get(userId).clone();
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}

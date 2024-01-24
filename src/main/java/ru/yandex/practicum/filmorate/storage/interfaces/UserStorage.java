package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User create(User user);

    public User update(User user);

    public void delete(Integer userId);

    public User getById(Integer userId);

    public List<User> getAll();
}

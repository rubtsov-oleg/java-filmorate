package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendStorage {
    Friend create(Friend friendEntry);

    void delete(Integer friendEntryId);

    List<Friend> getByUser(User user);

    Optional<Friend> getByUserAndFriend(User user, User friend);

    Friend update(Friend friend);
}
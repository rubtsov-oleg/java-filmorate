package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getById(Integer userId) {
        return userStorage.getById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        if (friendStorage.getByUserAndFriend(user, friend).isPresent()) {
            throw new AlreadyExistsException("User with ID " + friendId + " already in friend-list");
        }

        Friend userFriendEntry = new Friend();
        userFriendEntry.setUserId(user);
        userFriendEntry.setFriendId(friend);
        friendStorage.create(userFriendEntry);

        Friend friendFriendEntry = new Friend();
        friendFriendEntry.setUserId(friend);
        friendFriendEntry.setFriendId(user);
        friendStorage.create(friendFriendEntry);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        Optional<Friend> userFriendEntry = friendStorage.getByUserAndFriend(user, friend);
        if (userFriendEntry.isEmpty()) {
            throw new NoSuchElementException("User with ID " + friendId + " not in friend-list");
        }
        friendStorage.delete(userFriendEntry.get().getId());

        Optional<Friend> friendFriendEntry = friendStorage.getByUserAndFriend(friend, user);
        friendFriendEntry.ifPresent(value -> friendStorage.delete(value.getId()));
    }

    public List<User> getFriends(Integer userId) {
        User user = userStorage.getById(userId);
        List<Friend> friendEntries = friendStorage.getByUser(user);
        List<User> friends = new ArrayList<>();
        for (Friend friendEntry : friendEntries) {
            friends.add(userStorage.getById(friendEntry.getFriendId().getId()));
        }
        friends.sort(Comparator.comparingInt(User::getId));
        return friends;
    }


    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<Friend> userFriendEntries = friendStorage.getByUser(userStorage.getById(userId));
        List<Friend> otherFriendEntries = friendStorage.getByUser(userStorage.getById(otherId));

        Set<Integer> userFriends = userFriendEntries.stream()
                .map(friend -> friend.getFriendId().getId())
                .collect(Collectors.toSet());

        Set<Integer> otherFriends = otherFriendEntries.stream()
                .map(friend -> friend.getFriendId().getId())
                .collect(Collectors.toSet());

        userFriends.retainAll(otherFriends);
        return userFriends.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}

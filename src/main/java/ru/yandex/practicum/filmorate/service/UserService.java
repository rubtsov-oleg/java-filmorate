package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.SelfFriendshipException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.FriendStatus;
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
        if (userId.equals(friendId)) {
            throw new SelfFriendshipException("User cannot be a friend to themselves.");
        }

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (friendStorage.getByUserAndFriend(user, friend).isPresent()) {
            throw new AlreadyExistsException("User with ID " + friendId + " already in friend-list");
        }

        Optional<Friend> friendFriendEntryOpt = friendStorage.getByUserAndFriend(friend, user);
        if (friendFriendEntryOpt.isPresent()) {
            Friend friendFriendEntry = friendFriendEntryOpt.get();
            friendFriendEntry.setFriendStatus(FriendStatus.CONFIRMED);
            friendStorage.update(friendFriendEntry);
            return;
        }

        Friend userFriendEntry = new Friend();
        userFriendEntry.setUserId(user.getId());
        userFriendEntry.setFriendId(friend.getId());
        userFriendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
        friendStorage.create(userFriendEntry);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        Optional<Friend> userFriendEntryOpt = friendStorage.getByUserAndFriend(user, friend);
        if (userFriendEntryOpt.isPresent()) {
            friendStorage.delete(userFriendEntryOpt.get().getId());
            return;
        }

        Optional<Friend> friendFriendEntryOpt = friendStorage.getByUserAndFriend(friend, user);
        if (friendFriendEntryOpt.isPresent()) {
            Friend friendFriendEntry = friendFriendEntryOpt.get();
            friendFriendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
            friendStorage.update(friendFriendEntry);
            return;
        }

        throw new NoSuchElementException("User with ID " + friendId + " not in friend-list");
    }

    public List<User> getFriends(Integer userId) {
        User user = userStorage.getById(userId);
        List<Friend> friendEntries = friendStorage.getByUser(user);
        List<User> friends = new ArrayList<>();
        for (Friend friendEntry : friendEntries) {
            if (friendEntry.getUserId().equals(userId)) {
                friends.add(userStorage.getById(friendEntry.getFriendId()));
            } else {
                friends.add(userStorage.getById(friendEntry.getUserId()));
            }
        }
        friends.sort(Comparator.comparingInt(User::getId));
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<Friend> userFriendEntries = friendStorage.getByUser(userStorage.getById(userId));
        List<Friend> otherFriendEntries = friendStorage.getByUser(userStorage.getById(otherId));

        Set<Integer> userFriends = userFriendEntries.stream()
                .map(friend -> friend.getUserId().equals(userId) ? friend.getFriendId() : friend.getUserId())
                .collect(Collectors.toSet());

        Set<Integer> otherFriends = otherFriendEntries.stream()
                .map(friend -> friend.getUserId().equals(otherId) ? friend.getFriendId() : friend.getUserId())
                .collect(Collectors.toSet());

        userFriends.retainAll(otherFriends);
        return userFriends.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}

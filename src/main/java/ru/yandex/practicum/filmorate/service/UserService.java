package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();
        if (userFriends.contains(friendId)) {
            throw new AlreadyExistsException("User with ID " + friendId + " already in friend-list");
        }
        userFriends.add(friendId);
        friendFriends.add(userId);
        user.setFriends(userFriends);
        friend.setFriends(friendFriends);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();
        if (!userFriends.contains(friendId)) {
            throw new NoSuchElementException("User with ID " + friendId + " not in friend-list");
        }
        userFriends.remove(friendId);
        friendFriends.remove(userId);
        user.setFriends(userFriends);
        friend.setFriends(friendFriends);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<User> getFriends(Integer userId) {
        User user = userStorage.getById(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            friends.add(userStorage.getById(friendId));
        }
        friends.sort(Comparator.comparingInt(User::getId));
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = userStorage.getById(userId);
        User other = userStorage.getById(otherId);
        Set<Integer> userFriends = new HashSet<>(user.getFriends());
        Set<Integer> otherFriends = new HashSet<>(other.getFriends());
        userFriends.retainAll(otherFriends);
        List<User> commonFriends = new ArrayList<>();
        for (Integer intersectionId : userFriends) {
            commonFriends.add(userStorage.getById(intersectionId));
        }
        return commonFriends;
    }
}

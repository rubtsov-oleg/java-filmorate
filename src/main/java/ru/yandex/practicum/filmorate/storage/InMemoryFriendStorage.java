package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;

import java.util.*;

@Component
public class InMemoryFriendStorage implements FriendStorage {
    private final HashMap<Integer, Friend> friends = new HashMap<>();

    public Friend create(Friend friendEntry) {
        friendEntry.setId(IdIterator.getFriendId());
        friends.put(friendEntry.getId(), friendEntry);
        return friendEntry;
    }

    public void delete(Integer friendEntryId) {
        if (!friends.containsKey(friendEntryId)) {
            throw new NoSuchElementException("Friend Entry with ID " + friendEntryId + " not found");
        }
        friends.remove(friendEntryId);
    }

    public List<Friend> getByUser(User user) {
        List<Friend> userFriends = new ArrayList<>();
        for (Friend friendEntry : friends.values()) {
            if (friendEntry.getUserId().equals(user)) {
                userFriends.add(friendEntry);
            }
        }
        return userFriends;
    }

    public Optional<Friend> getByUserAndFriend(User user, User friend) {
        for (Friend friendEntry : friends.values()) {
            if (friendEntry.getUserId().equals(user) && (friendEntry.getFriendId().equals(friend))) {
                return Optional.of(friendEntry);
            }
        }
        return Optional.empty();
    }
}

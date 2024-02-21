package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InDbFriendStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private InDbFriendStorage inDbFriendStorage;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        DatabaseUtil databaseUtil = new DatabaseUtil(jdbcTemplate);
        inDbFriendStorage = new InDbFriendStorage(jdbcTemplate, databaseUtil);
        InDbUserStorage inDbUserStorage = new InDbUserStorage(jdbcTemplate, databaseUtil);
        user1 = createUser(inDbUserStorage);
        user2 = createUser(inDbUserStorage);
        user3 = createUser(inDbUserStorage);
    }

    @Test
    public void testCreate() {
        Friend friendEntry = new Friend();
        friendEntry.setUserId(user1.getId());
        friendEntry.setFriendId(user2.getId());
        friendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
        inDbFriendStorage.create(friendEntry);

        Optional<Friend> savedFriendEntry = inDbFriendStorage.getByUserAndFriend(user1, user2);

        assertTrue(savedFriendEntry.isPresent());
        assertEquals(savedFriendEntry.get().getUserId(), user1.getId());
        assertEquals(savedFriendEntry.get().getFriendId(), user2.getId());
    }

    @Test
    public void testUpdate() {
        Friend friendEntry = new Friend();
        friendEntry.setUserId(user1.getId());
        friendEntry.setFriendId(user2.getId());
        friendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
        Friend savedFriendEntry = inDbFriendStorage.create(friendEntry);

        assertEquals(savedFriendEntry.getFriendStatus(), FriendStatus.UNCONFIRMED);

        savedFriendEntry.setFriendStatus(FriendStatus.CONFIRMED);
        inDbFriendStorage.update(savedFriendEntry);
        Friend savedFriendEntryAfterChange = inDbFriendStorage.getByUserAndFriend(user1, user2).get();

        assertEquals(savedFriendEntryAfterChange.getFriendStatus(), FriendStatus.CONFIRMED);

        Friend emptyFriendEntry = new Friend();
        emptyFriendEntry.setId(5);
        emptyFriendEntry.setUserId(user1.getId());
        emptyFriendEntry.setFriendId(user3.getId());
        emptyFriendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);

        assertThrows(NoSuchElementException.class, () -> {
            inDbFriendStorage.update(emptyFriendEntry);
        });
    }

    @Test
    public void getByUser() {
        List<Friend> emptySavedFriends = inDbFriendStorage.getByUser(user1);

        Friend friendEntry = new Friend();
        friendEntry.setUserId(user1.getId());
        friendEntry.setFriendId(user2.getId());
        friendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
        inDbFriendStorage.create(friendEntry);

        List<Friend> savedFriendsWithOneItem = inDbFriendStorage.getByUser(user1);

        Friend friendEntry2 = new Friend();
        friendEntry2.setUserId(user3.getId());
        friendEntry2.setFriendId(user1.getId());
        friendEntry2.setFriendStatus(FriendStatus.CONFIRMED);
        inDbFriendStorage.create(friendEntry2);

        List<Friend> savedFriendsWithTwoItems = inDbFriendStorage.getByUser(user1);

        assertEquals(emptySavedFriends.size(), 0);
        assertEquals(savedFriendsWithOneItem.size(), 1);
        assertEquals(savedFriendsWithTwoItems.size(), 2);
    }

    @Test
    public void testDelete() {
        Friend friendEntry = new Friend();
        friendEntry.setUserId(user1.getId());
        friendEntry.setFriendId(user2.getId());
        friendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
        inDbFriendStorage.create(friendEntry);
        Optional<Friend> savedFriendEntry = inDbFriendStorage.getByUserAndFriend(user1, user2);

        inDbFriendStorage.delete(savedFriendEntry.get().getId());

        assertTrue(inDbFriendStorage.getByUserAndFriend(user1, user2).isEmpty());
        assertThrows(NoSuchElementException.class, () -> {
            inDbFriendStorage.delete(2);
        });
    }

    @Test
    public void getByUserAndFriend() {
        Friend friendEntry = new Friend();
        friendEntry.setUserId(user1.getId());
        friendEntry.setFriendId(user2.getId());
        friendEntry.setFriendStatus(FriendStatus.UNCONFIRMED);
        inDbFriendStorage.create(friendEntry);

        Optional<Friend> savedFriend = inDbFriendStorage.getByUserAndFriend(user1, user2);
        Optional<Friend> savedFriend2 = inDbFriendStorage.getByUserAndFriend(user2, user3);

        assertTrue(savedFriend.isPresent());
        assertTrue(savedFriend2.isEmpty());
        assertEquals(savedFriend.get().getUserId(), user1.getId());
        assertEquals(savedFriend.get().getFriendId(), user2.getId());
    }

    public User createUser(InDbUserStorage inDbUserStorage) {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test");
        user.setBirthday(LocalDate.of(2020, 5, 3));
        user.setEmail("test@mail.ru");
        return inDbUserStorage.create(user);
    }
}

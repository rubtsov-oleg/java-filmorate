package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("USER CREATE INPUT - {}!", user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(IdIterator.getUserId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("USER UPDATE INPUT - {}!", user);
        Integer userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
        users.put(userId, user);
        return user;
    }
}


package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        log.info("USER UPDATE INPUT - {}!", user);
        Integer userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
            return ResponseEntity.ok(user);
        } else {
            log.info("USER UPDATE ERROR - User with ID {} not found!", userId);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User with ID " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.info("VALIDATION ERRORS - {}!", errors);
        return errors;
    }
}


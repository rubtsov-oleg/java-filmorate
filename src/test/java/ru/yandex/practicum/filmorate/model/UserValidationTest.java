package ru.yandex.practicum.filmorate.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

public class UserValidationTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testUserSuccess() {
        User user = new User();
        user.setEmail("testemail@mail.ru");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().minusYears(5));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserFailEmail() {
        User user = new User();
        user.setEmail("testemail");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().minusYears(5));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }

    @Test
    public void testUserFailLogin() {
        User user = new User();
        user.setEmail("testemail@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(5));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }

    @Test
    public void testUserFailBirthday() {
        User user = new User();
        user.setEmail("testemail@mail.ru");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().plusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(violations.size(), 1);
    }
}
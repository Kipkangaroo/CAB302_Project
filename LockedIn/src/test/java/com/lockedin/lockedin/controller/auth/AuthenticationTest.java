package com.lockedin.lockedin.controller.auth;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Authentication utility class.
 * Verifies email and password validation rules using
 * representative valid and invalid input cases.
 */

public class AuthenticationTest {

    // ---- isValidEmail ----

    @Test
    void isValidEmail_acceptsTypicalEmail() {
        assertTrue(Authentication.isValidEmail("user@example.com"));
    }

    @Test
    void isValidEmail_acceptsSubdomain() {
        assertTrue(Authentication.isValidEmail("user@mail.example.org"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutAt() {
        assertFalse(Authentication.isValidEmail("userexample.com"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutDomain() {
        assertFalse(Authentication.isValidEmail("user@"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutTld() {
        assertFalse(Authentication.isValidEmail("user@example"));
    }

    @Test
    void isValidEmail_rejectsEmptyString() {
        assertFalse(Authentication.isValidEmail(""));
    }

    // ---- isValidPassword ----

    @Test
    void isValidPassword_acceptsValidPassword() {
        assertTrue(Authentication.isValidPassword("Secure@123"));
    }

    @Test
    void isValidPassword_rejectsTooShort() {
        assertFalse(Authentication.isValidPassword("Ab!1234"));
    }

    @Test
    void isValidPassword_rejectsNoUppercase() {
        assertFalse(Authentication.isValidPassword("password1!"));
    }

    @Test
    void isValidPassword_rejectsNoLowercase() {
        assertFalse(Authentication.isValidPassword("PASSWORD1!"));
    }

    @Test
    void isValidPassword_rejectsNoSpecialCharacter() {
        assertFalse(Authentication.isValidPassword("Password123"));
    }

    // ---- authenticate(email, password, UserDAO) ----

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";

    private User makeUser(String email) {
        return new User(0, "Jane", "Doe", email,
                LocalDate.of(2000, 1, 1), 170.0, 65.0, "Password1!", "Maintain Fitness");
    }

    @Test
    void authenticate_correctCredentials_returnsPresent() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser(makeUser("jane@test.com"));

        Optional<User> result = Authentication.authenticate("jane@test.com", "Password1!", userDAO);
        assertTrue(result.isPresent());
    }

    @Test
    void authenticate_wrongPassword_returnsEmpty() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser(makeUser("jane2@test.com"));

        Optional<User> result = Authentication.authenticate("jane2@test.com", "WrongPass1!", userDAO);
        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_unknownEmail_returnsEmpty() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);

        Optional<User> result = Authentication.authenticate("nobody@test.com", "Password1!", userDAO);
        assertTrue(result.isEmpty());
    }
}
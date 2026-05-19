package com.lockedin.lockedin.controller.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Unit tests for {@link Authentication}. Verifies email and password validation and login against
 * an in-memory database.
 */
public class AuthenticationTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";

    private Authentication auth;

    @BeforeEach
    void setUp() throws SQLException {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        auth = new Authentication(new UserDAO(conn));
    }

    // ---- isValidEmail ----

    @Test
    void isValidEmail_acceptsTypicalEmail() {
        assertTrue(auth.isValidEmail("user@example.com"));
    }

    @Test
    void isValidEmail_acceptsSubdomain() {
        assertTrue(auth.isValidEmail("user@mail.example.org"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutAt() {
        assertFalse(auth.isValidEmail("userexample.com"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutDomain() {
        assertFalse(auth.isValidEmail("user@"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutTld() {
        assertFalse(auth.isValidEmail("user@example"));
    }

    @Test
    void isValidEmail_rejectsEmptyString() {
        assertFalse(auth.isValidEmail(""));
    }

    // ---- isValidPassword ----

    @Test
    void isValidPassword_acceptsValidPassword() {
        assertTrue(auth.isValidPassword("Secure@123"));
    }

    @Test
    void isValidPassword_rejectsTooShort() {
        assertFalse(auth.isValidPassword("Ab!1234"));
    }

    @Test
    void isValidPassword_rejectsNoUppercase() {
        assertFalse(auth.isValidPassword("password1!"));
    }

    @Test
    void isValidPassword_rejectsNoLowercase() {
        assertFalse(auth.isValidPassword("PASSWORD1!"));
    }

    @Test
    void isValidPassword_rejectsNoSpecialCharacter() {
        assertFalse(auth.isValidPassword("Password123"));
    }

    // ---- authenticate ----

    private User makeUser(String email) {
        return new User(
                0,
                "Jane",
                "Doe",
                email,
                LocalDate.of(2000, 1, 1),
                170.0,
                65.0,
                "Male",
                ActivityLevel.SEDENTARY,
                FitnessGoal.MAINTAIN_FITNESS,
                "Password1!");
    }

    @Test
    void authenticate_correctCredentials_returnsPresent() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser(makeUser("jane@test.com"));
        Authentication login = new Authentication(userDAO);

        Optional<User> result = login.authenticate("jane@test.com", "Password1!");
        assertTrue(result.isPresent());
    }

    @Test
    void authenticate_wrongPassword_returnsEmpty() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser(makeUser("jane2@test.com"));
        Authentication login = new Authentication(userDAO);

        Optional<User> result = login.authenticate("jane2@test.com", "WrongPass1!");
        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_unknownEmail_returnsEmpty() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        Authentication login = new Authentication(userDAO);

        Optional<User> result = login.authenticate("nobody@test.com", "Password1!");
        assertTrue(result.isEmpty());
    }
}

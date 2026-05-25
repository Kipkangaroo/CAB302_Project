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
 * Unit tests for {@link Authentication}, covering validation and login against an in-memory database.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class AuthenticationTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";

    private Authentication auth;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() throws SQLException {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        auth = new Authentication(new UserDAO(conn));
    }

    // ---- isValidEmail ----


    /**
     * Verifies isValidEmail: accepts Typical Email.
     */
    @Test
    void isValidEmail_acceptsTypicalEmail() {
        assertTrue(auth.isValidEmail("user@example.com"));
    }


    /**
     * Verifies isValidEmail: accepts Subdomain.
     */
    @Test
    void isValidEmail_acceptsSubdomain() {
        assertTrue(auth.isValidEmail("user@mail.example.org"));
    }


    /**
     * Verifies isValidEmail: rejects Email Without At.
     */
    @Test
    void isValidEmail_rejectsEmailWithoutAt() {
        assertFalse(auth.isValidEmail("userexample.com"));
    }


    /**
     * Verifies isValidEmail: rejects Email Without Domain.
     */
    @Test
    void isValidEmail_rejectsEmailWithoutDomain() {
        assertFalse(auth.isValidEmail("user@"));
    }


    /**
     * Verifies isValidEmail: rejects Email Without Tld.
     */
    @Test
    void isValidEmail_rejectsEmailWithoutTld() {
        assertFalse(auth.isValidEmail("user@example"));
    }


    /**
     * Verifies isValidEmail: rejects Empty String.
     */
    @Test
    void isValidEmail_rejectsEmptyString() {
        assertFalse(auth.isValidEmail(""));
    }

    // ---- isValidPassword ----


    /**
     * Verifies isValidPassword: accepts Valid Password.
     */
    @Test
    void isValidPassword_acceptsValidPassword() {
        assertTrue(auth.isValidPassword("Secure@123"));
    }


    /**
     * Verifies isValidPassword: rejects Too Short.
     */
    @Test
    void isValidPassword_rejectsTooShort() {
        assertFalse(auth.isValidPassword("Ab!1234"));
    }


    /**
     * Verifies isValidPassword: rejects No Uppercase.
     */
    @Test
    void isValidPassword_rejectsNoUppercase() {
        assertFalse(auth.isValidPassword("password1!"));
    }


    /**
     * Verifies isValidPassword: rejects No Lowercase.
     */
    @Test
    void isValidPassword_rejectsNoLowercase() {
        assertFalse(auth.isValidPassword("PASSWORD1!"));
    }


    /**
     * Verifies isValidPassword: rejects No Special Character.
     */
    @Test
    void isValidPassword_rejectsNoSpecialCharacter() {
        assertFalse(auth.isValidPassword("Password123"));
    }

    // ---- authenticate ----

    /**
     * Builds a test user with the given email and default profile values.
     *
     * @param email login email
     * @return user ready for persistence
     */
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

    /**
     * Verifies authenticate: returns present for correct credentials.
     */
    @Test
    void authenticate_correctCredentials_returnsPresent() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser(makeUser("jane@test.com"));
        Authentication login = new Authentication(userDAO);

        Optional<User> result = login.authenticate("jane@test.com", "Password1!");
        assertTrue(result.isPresent());
    }

    /**
     * Verifies authenticate: returns empty for wrong password.
     */
    @Test
    void authenticate_wrongPassword_returnsEmpty() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser(makeUser("jane2@test.com"));
        Authentication login = new Authentication(userDAO);

        Optional<User> result = login.authenticate("jane2@test.com", "WrongPass1!");
        assertTrue(result.isEmpty());
    }

    /**
     * Verifies authenticate: returns empty for unknown email.
     */
    @Test
    void authenticate_unknownEmail_returnsEmpty() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        UserDAO userDAO = new UserDAO(conn);
        Authentication login = new Authentication(userDAO);

        Optional<User> result = login.authenticate("nobody@test.com", "Password1!");
        assertTrue(result.isEmpty());
    }
}

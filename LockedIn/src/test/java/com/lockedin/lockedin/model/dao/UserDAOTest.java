package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Unit tests for UserDAO using an isolated in-memory SQLite database.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class UserDAOTest {
    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private UserDAO userDAO;

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
                FitnessGoal.BUILD_MUSCLE,
                "Password1!");
    }

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        userDAO = new UserDAO(conn);
    }


    /**
     * Verifies createUser: returns True.
     */
    @Test
    void createUser_returnsTrue() {
        assertTrue(userDAO.createUser(makeUser("a@test.com")));
    }


    /**
     * Verifies createUser: assigns Generated Id.
     */
    @Test
    void createUser_assignsGeneratedId() {
        User user = makeUser("b@test.com");
        userDAO.createUser(user);
        assertTrue(user.getId() > 0);
    }


    /**
     * Verifies getUserById: returns User when Exists.
     */
    @Test
    void getUserById_returnsUser_whenExists() {
        User user = makeUser("c@test.com");
        userDAO.createUser(user);
        Optional<User> found = userDAO.getUserById(user.getId());
        assertTrue(found.isPresent());
    }


    /**
     * Verifies getUserById: returns Empty when Not Found.
     */
    @Test
    void getUserById_returnsEmpty_whenNotFound() {
        Optional<User> found = userDAO.getUserById(999);
        assertTrue(found.isEmpty());
    }


    /**
     * Verifies getUserByEmail: returns User when Exists.
     */
    @Test
    void getUserByEmail_returnsUser_whenExists() {
        userDAO.createUser(makeUser("d@test.com"));
        Optional<User> found = userDAO.getUserByEmail("d@test.com");
        assertTrue(found.isPresent());
        assertEquals("d@test.com", found.get().getEmail());
    }


    /**
     * Verifies getUserByEmail: returns Empty when Not Found.
     */
    @Test
    void getUserByEmail_returnsEmpty_whenNotFound() {
        Optional<User> found = userDAO.getUserByEmail("nobody@test.com");
        assertTrue(found.isEmpty());
    }


    /**
     * Verifies authenticate: returns True with Correct Password.
     */
    @Test
    void authenticate_returnsTrue_withCorrectPassword() {
        userDAO.createUser(makeUser("e@test.com"));
        assertTrue(userDAO.authenticate("e@test.com", "Password1!"));
    }


    /**
     * Verifies authenticate: returns False with Wrong Password.
     */
    @Test
    void authenticate_returnsFalse_withWrongPassword() {
        userDAO.createUser(makeUser("f@test.com"));
        assertFalse(userDAO.authenticate("f@test.com", "wrongpass"));
    }


    /**
     * Verifies authenticate: returns False when Email Not Found.
     */
    @Test
    void authenticate_returnsFalse_whenEmailNotFound() {
        assertFalse(userDAO.authenticate("ghost@test.com", "Password1!"));
    }


    /**
     * Verifies createUser: preserves All Fields.
     */
    @Test
    void createUser_preservesAllFields() {
        User user = makeUser("g@test.com");
        userDAO.createUser(user);
        User found = userDAO.getUserById(user.getId()).orElseThrow();
        assertEquals("Jane", found.getFirstName());
        assertEquals("Doe", found.getLastName());
        assertEquals("g@test.com", found.getEmail());
        assertEquals(LocalDate.of(2000, 1, 1), found.getDateOfBirth());
        assertEquals(170.0, found.getHeight());
        assertEquals(65.0, found.getWeight());
        assertEquals(FitnessGoal.BUILD_MUSCLE, found.getFitnessGoal());
    }


    /**
     * Verifies updateFirstName: returns True and Persists.
     */
    @Test
    void updateFirstName_returnsTrue_andPersists() {
        User user = makeUser("names@test.com");
        userDAO.createUser(user);
        int id = user.getId();
        assertTrue(userDAO.updateFirstName(id, "Janet"));
        User found = userDAO.getUserById(id).orElseThrow();
        assertEquals("Janet", found.getFirstName());
        assertEquals("Doe", found.getLastName());
    }


    /**
     * Verifies updateFirstName: returns False when Id Missing.
     */
    @Test
    void updateFirstName_returnsFalse_whenIdMissing() {
        assertFalse(userDAO.updateFirstName(99999, "A"));
    }
}

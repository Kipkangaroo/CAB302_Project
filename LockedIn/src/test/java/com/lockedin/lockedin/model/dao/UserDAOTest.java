package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private UserDAO userDAO;

    private User makeUser(String email) {
        return new User(0, "Jane", "Doe", email,
                LocalDate.of(2000, 1, 1), 170.0, 65.0,
                "Password1!", "Build Muscle");
    }

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        userDAO = new UserDAO(conn);
    }

    @Test
    void createUser_returnsTrue() {
        assertTrue(userDAO.createUser(makeUser("a@test.com")));
    }

    @Test
    void createUser_assignsGeneratedId() {
        User user = makeUser("b@test.com");
        userDAO.createUser(user);
        assertTrue(user.getId() > 0);
    }

    @Test
    void getUserById_returnsUser_whenExists() {
        User user = makeUser("c@test.com");
        userDAO.createUser(user);
        Optional<User> found = userDAO.getUserById(user.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void getUserById_returnsEmpty_whenNotFound() {
        Optional<User> found = userDAO.getUserById(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void getUserByEmail_returnsUser_whenExists() {
        userDAO.createUser(makeUser("d@test.com"));
        Optional<User> found = userDAO.getUserByEmail("d@test.com");
        assertTrue(found.isPresent());
        assertEquals("d@test.com", found.get().getEmail());
    }

    @Test
    void getUserByEmail_returnsEmpty_whenNotFound() {
        Optional<User> found = userDAO.getUserByEmail("nobody@test.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void authenticate_returnsTrue_withCorrectPassword() {
        userDAO.createUser(makeUser("e@test.com"));
        assertTrue(userDAO.authenticate("e@test.com", "Password1!"));
    }

    @Test
    void authenticate_returnsFalse_withWrongPassword() {
        userDAO.createUser(makeUser("f@test.com"));
        assertFalse(userDAO.authenticate("f@test.com", "wrongpass"));
    }

    @Test
    void authenticate_returnsFalse_whenEmailNotFound() {
        assertFalse(userDAO.authenticate("ghost@test.com", "Password1!"));
    }

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
        assertEquals("Build Muscle", found.getFitnessGoal());
    }
}
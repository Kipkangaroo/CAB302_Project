package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.UserProgress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserProgressDAOTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private static final int USER_ID = 1;
    private UserProgressDAO progressDAO;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        progressDAO = new UserProgressDAO(conn);
    }

    private void insertRow() {
        UserProgress up = new UserProgress(
                0, USER_ID, FitnessGoal.MAINTAIN_FITNESS, 70.0, 2000.0,
                LocalDate.of(2024, 1, 1));
        progressDAO.addUserProgress(up);
    }

    @Test
    void getSessionTarget_returnsThree_whenNotSet() {
        assertEquals(3, progressDAO.getSessionTarget(USER_ID));
    }

    @Test
    void updateSessionTarget_persistsValue() {
        insertRow();
        progressDAO.updateSessionTarget(USER_ID, 5);
        assertEquals(5, progressDAO.getSessionTarget(USER_ID));
    }

    @Test
    void getSessionTarget_returnsUpdatedValue_afterSave() {
        insertRow();
        progressDAO.updateSessionTarget(USER_ID, 7);
        assertEquals(7, progressDAO.getSessionTarget(USER_ID));
    }
}

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

    @Test
    void getDailyTargetCalories_returnsRecordedValue() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        UserProgress up = new UserProgress(
                0, USER_ID, FitnessGoal.MAINTAIN_FITNESS, 70.0, 2200.0, date);
        progressDAO.addUserProgress(up);

        assertEquals(2200.0, progressDAO.getDailyTargetCalories(USER_ID, date));
    }
}

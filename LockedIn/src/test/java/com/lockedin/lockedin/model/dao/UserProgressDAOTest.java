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

    @Test
    void addUserProgress_assignsGeneratedId() {
        UserProgress up = new UserProgress(
                0, USER_ID, FitnessGoal.BUILD_MUSCLE, 68.0, 2500.0, LocalDate.of(2024, 2, 1));
        assertTrue(progressDAO.addUserProgress(up));
        assertTrue(up.getId() > 0);
    }

    @Test
    void getUserProgressByDate_returnsEmpty_whenMissing() {
        assertTrue(progressDAO.getUserProgressByDate(USER_ID, LocalDate.of(2020, 1, 1)).isEmpty());
    }

    @Test
    void getUserProgressByDate_returnsProgress_whenExists() {
        LocalDate date = LocalDate.of(2024, 3, 10);
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.LOSE_WEIGHT, 72.0, 1800.0, date));
        UserProgress found = progressDAO.getUserProgressByDate(USER_ID, date).orElseThrow();
        assertEquals(72.0, found.getWeight());
        assertEquals(FitnessGoal.LOSE_WEIGHT, found.getFitnessGoal());
    }

    @Test
    void addUserProgress_updatesExistingRowForSameDate() {
        LocalDate date = LocalDate.of(2024, 4, 1);
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 70.0, 2400.0, date));
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.MAINTAIN_FITNESS, 71.0, 2300.0, date));
        UserProgress found = progressDAO.getUserProgressByDate(USER_ID, date).orElseThrow();
        assertEquals(71.0, found.getWeight());
        assertEquals(2300.0, found.getTargetCalories());
        assertEquals(FitnessGoal.MAINTAIN_FITNESS, found.getFitnessGoal());
    }

    @Test
    void getUserProgressHistory_returnsEntriesForUser() {
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 70.0, 2400.0, LocalDate.of(2024, 5, 1)));
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 71.0, 2450.0, LocalDate.of(2024, 5, 2)));
        assertEquals(2, progressDAO.getUserProgressHistory(USER_ID).size());
    }

    @Test
    void getLatestUserProgress_returnsLatestOnOrBeforeDate() {
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 70.0, 2400.0, LocalDate.of(2024, 6, 1)));
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 72.0, 2500.0, LocalDate.of(2024, 6, 10)));
        UserProgress latest =
                progressDAO.getLatestUserProgress(USER_ID, LocalDate.of(2024, 6, 5)).orElseThrow();
        assertEquals(LocalDate.of(2024, 6, 1), latest.getEffectiveFrom());
    }

    @Test
    void deleteAllForUser_removesHistory() {
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 70.0, 2400.0, LocalDate.of(2024, 7, 1)));
        progressDAO.deleteAllForUser(USER_ID);
        assertTrue(progressDAO.getUserProgressHistory(USER_ID).isEmpty());
    }

    @Test
    void getDailyWeightForRange_forwardFillsBetweenEntries() {
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 60.0, 2000.0, LocalDate.of(2024, 8, 1)));
        progressDAO.addUserProgress(
                new UserProgress(0, USER_ID, FitnessGoal.BUILD_MUSCLE, 65.0, 2100.0, LocalDate.of(2024, 8, 5)));
        var weights = progressDAO.getDailyWeightForRange(
                USER_ID, LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 3));
        assertEquals(60.0, weights.get(LocalDate.of(2024, 8, 1)));
        assertEquals(60.0, weights.get(LocalDate.of(2024, 8, 2)));
        assertEquals(60.0, weights.get(LocalDate.of(2024, 8, 3)));
    }
}

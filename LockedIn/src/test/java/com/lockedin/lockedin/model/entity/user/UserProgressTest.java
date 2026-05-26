package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * Unit tests for UserProgress, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class UserProgressTest {


    /**
     * Verifies singleArgConstructor: sets User Id.
     */
    @Test
    void singleArgConstructor_setsUserId() {
        UserProgress progress = new UserProgress(7);
        assertEquals(7, progress.getUserId());
    }


    /**
     * Verifies fullConstructor: sets All Fields.
     */
    @Test
    void fullConstructor_setsAllFields() {
        LocalDate date = LocalDate.of(2024, 9, 1);
        UserProgress progress =
                new UserProgress(1, 7, FitnessGoal.LOSE_WEIGHT, 68.5, 1900.0, date);
        assertEquals(1, progress.getId());
        assertEquals(7, progress.getUserId());
        assertEquals(FitnessGoal.LOSE_WEIGHT, progress.getFitnessGoal());
        assertEquals(68.5, progress.getWeight());
        assertEquals(1900.0, progress.getTargetCalories());
        assertEquals(date, progress.getEffectiveFrom());
    }


    /**
     * Verifies setId: updates Value.
     */
    @Test
    void setId_updatesValue() {
        UserProgress progress = new UserProgress(1, 2, FitnessGoal.BUILD_MUSCLE, 70, 2400, LocalDate.now());
        progress.setId(10);
        assertEquals(10, progress.getId());
    }


    /**
     * Verifies setFitnessGoal: updates Value.
     */
    @Test
    void setFitnessGoal_updatesValue() {
        UserProgress progress = new UserProgress(2);
        progress.setFitnessGoal(FitnessGoal.MAINTAIN_FITNESS);
        assertEquals(FitnessGoal.MAINTAIN_FITNESS, progress.getFitnessGoal());
    }


    /**
     * Verifies setWeight: updates Value.
     */
    @Test
    void setWeight_updatesValue() {
        UserProgress progress = new UserProgress(2);
        progress.setWeight(75.2);
        assertEquals(75.2, progress.getWeight());
    }


    /**
     * Verifies setTargetCalories: updates Value.
     */
    @Test
    void setTargetCalories_updatesValue() {
        UserProgress progress = new UserProgress(2);
        progress.setTargetCalories(2200);
        assertEquals(2200, progress.getTargetCalories());
    }


    /**
     * Verifies setEffectiveFrom: updates Value.
     */
    @Test
    void setEffectiveFrom_updatesValue() {
        UserProgress progress = new UserProgress(2);
        LocalDate date = LocalDate.of(2025, 1, 15);
        progress.setEffectiveFrom(date);
        assertEquals(date, progress.getEffectiveFrom());
    }


    /**
     * Verifies getUserId: is Immutable From Constructor.
     */
    @Test
    void getUserId_isImmutableFromConstructor() {
        UserProgress progress = new UserProgress(3, 9, FitnessGoal.BUILD_MUSCLE, 80, 2600, LocalDate.now());
        assertEquals(9, progress.getUserId());
    }
}

package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class UserProgressTest {

    @Test
    void singleArgConstructor_setsUserId() {
        UserProgress progress = new UserProgress(7);
        assertEquals(7, progress.getUserId());
    }

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

    @Test
    void setId_updatesValue() {
        UserProgress progress = new UserProgress(1, 2, FitnessGoal.BUILD_MUSCLE, 70, 2400, LocalDate.now());
        progress.setId(10);
        assertEquals(10, progress.getId());
    }

    @Test
    void setFitnessGoal_updatesValue() {
        UserProgress progress = new UserProgress(2);
        progress.setFitnessGoal(FitnessGoal.MAINTAIN_FITNESS);
        assertEquals(FitnessGoal.MAINTAIN_FITNESS, progress.getFitnessGoal());
    }

    @Test
    void setWeight_updatesValue() {
        UserProgress progress = new UserProgress(2);
        progress.setWeight(75.2);
        assertEquals(75.2, progress.getWeight());
    }

    @Test
    void setTargetCalories_updatesValue() {
        UserProgress progress = new UserProgress(2);
        progress.setTargetCalories(2200);
        assertEquals(2200, progress.getTargetCalories());
    }

    @Test
    void setEffectiveFrom_updatesValue() {
        UserProgress progress = new UserProgress(2);
        LocalDate date = LocalDate.of(2025, 1, 15);
        progress.setEffectiveFrom(date);
        assertEquals(date, progress.getEffectiveFrom());
    }

    @Test
    void getUserId_isImmutableFromConstructor() {
        UserProgress progress = new UserProgress(3, 9, FitnessGoal.BUILD_MUSCLE, 80, 2600, LocalDate.now());
        assertEquals(9, progress.getUserId());
    }
}

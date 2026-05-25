package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FitnessGoalTest {

    @Test
    void fromDisplayName_returnsMatchingGoal() {
        assertEquals(FitnessGoal.LOSE_WEIGHT, FitnessGoal.fromDisplayName("Lose Weight"));
    }

    @Test
    void fromDisplayName_returnsNull_forUnknownLabel() {
        assertNull(FitnessGoal.fromDisplayName("Unknown"));
    }

    @Test
    void fromDisplayName_returnsNull_forNullInput() {
        assertNull(FitnessGoal.fromDisplayName(null));
    }

    @Test
    void getCalorieAdjustment_matchesEnumDefinition() {
        assertEquals(-500, FitnessGoal.LOSE_WEIGHT.getCalorieAdjustment());
        assertEquals(500, FitnessGoal.BUILD_MUSCLE.getCalorieAdjustment());
        assertEquals(0, FitnessGoal.MAINTAIN_FITNESS.getCalorieAdjustment());
    }

    @Test
    void macroRatios_sumToOne_forEachGoal() {
        for (FitnessGoal goal : FitnessGoal.values()) {
            double sum = goal.getProteinRatio() + goal.getCarbsRatio() + goal.getFatsRatio();
            assertEquals(1.0, sum, 0.001, goal.name());
        }
    }

    @Test
    void toString_returnsDisplayName() {
        assertEquals("Build Muscle", FitnessGoal.BUILD_MUSCLE.toString());
    }

    @Test
    void values_containsAllThreeGoals() {
        assertEquals(3, FitnessGoal.values().length);
    }

    @Test
    void getDisplayName_isUniqueAcrossValues() {
        long distinct =
                java.util.Arrays.stream(FitnessGoal.values())
                        .map(FitnessGoal::getDisplayName)
                        .distinct()
                        .count();
        assertEquals(FitnessGoal.values().length, distinct);
    }
}

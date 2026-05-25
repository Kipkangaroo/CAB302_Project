package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for FitnessGoal, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class FitnessGoalTest {


    /**
     * Verifies fromDisplayName: returns Matching Goal.
     */
    @Test
    void fromDisplayName_returnsMatchingGoal() {
        assertEquals(FitnessGoal.LOSE_WEIGHT, FitnessGoal.fromDisplayName("Lose Weight"));
    }


    /**
     * Verifies fromDisplayName: returns Null for Unknown Label.
     */
    @Test
    void fromDisplayName_returnsNull_forUnknownLabel() {
        assertNull(FitnessGoal.fromDisplayName("Unknown"));
    }


    /**
     * Verifies fromDisplayName: returns Null for Null Input.
     */
    @Test
    void fromDisplayName_returnsNull_forNullInput() {
        assertNull(FitnessGoal.fromDisplayName(null));
    }


    /**
     * Verifies getCalorieAdjustment: matches Enum Definition.
     */
    @Test
    void getCalorieAdjustment_matchesEnumDefinition() {
        assertEquals(-500, FitnessGoal.LOSE_WEIGHT.getCalorieAdjustment());
        assertEquals(500, FitnessGoal.BUILD_MUSCLE.getCalorieAdjustment());
        assertEquals(0, FitnessGoal.MAINTAIN_FITNESS.getCalorieAdjustment());
    }


    /**
     * Verifies macroRatios: sum To One for Each Goal.
     */
    @Test
    void macroRatios_sumToOne_forEachGoal() {
        for (FitnessGoal goal : FitnessGoal.values()) {
            double sum = goal.getProteinRatio() + goal.getCarbsRatio() + goal.getFatsRatio();
            assertEquals(1.0, sum, 0.001, goal.name());
        }
    }


    /**
     * Verifies toString: returns Display Name.
     */
    @Test
    void toString_returnsDisplayName() {
        assertEquals("Build Muscle", FitnessGoal.BUILD_MUSCLE.toString());
    }


    /**
     * Verifies values: contains All Three Goals.
     */
    @Test
    void values_containsAllThreeGoals() {
        assertEquals(3, FitnessGoal.values().length);
    }


    /**
     * Verifies getDisplayName: is Unique Across Values.
     */
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

package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for ActivityLevel, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class ActivityLevelTest {


    /**
     * Verifies fromDisplayName: returns Matching Level.
     */
    @Test
    void fromDisplayName_returnsMatchingLevel() {
        assertEquals(ActivityLevel.SEDENTARY, ActivityLevel.fromDisplayName("Sedentary (little/no exercise)"));
    }


    /**
     * Verifies fromDisplayName: returns Null for Unknown Label.
     */
    @Test
    void fromDisplayName_returnsNull_forUnknownLabel() {
        assertNull(ActivityLevel.fromDisplayName("Unknown"));
    }


    /**
     * Verifies fromDisplayName: returns Null for Null Input.
     */
    @Test
    void fromDisplayName_returnsNull_forNullInput() {
        assertNull(ActivityLevel.fromDisplayName(null));
    }


    /**
     * Verifies getTdeeMultiplier: increases With Activity.
     */
    @Test
    void getTdeeMultiplier_increasesWithActivity() {
        assertTrue(ActivityLevel.LIGHTLY_ACTIVE.getTdeeMultiplier() > ActivityLevel.SEDENTARY.getTdeeMultiplier());
        assertTrue(
                ActivityLevel.VERY_ACTIVE.getTdeeMultiplier()
                        > ActivityLevel.MODERATELY_ACTIVE.getTdeeMultiplier());
    }


    /**
     * Verifies toString: returns Display Name.
     */
    @Test
    void toString_returnsDisplayName() {
        assertEquals("Very active (6–7 days/week)", ActivityLevel.VERY_ACTIVE.toString());
    }


    /**
     * Verifies values: contains Five Levels.
     */
    @Test
    void values_containsFiveLevels() {
        assertEquals(5, ActivityLevel.values().length);
    }


    /**
     * Verifies getDisplayName: is Unique Across Values.
     */
    @Test
    void getDisplayName_isUniqueAcrossValues() {
        long distinct =
                java.util.Arrays.stream(ActivityLevel.values())
                        .map(ActivityLevel::getDisplayName)
                        .distinct()
                        .count();
        assertEquals(ActivityLevel.values().length, distinct);
    }


    /**
     * Verifies extraActive: has Highest Multiplier.
     */
    @Test
    void extraActive_hasHighestMultiplier() {
        double max =
                java.util.Arrays.stream(ActivityLevel.values())
                        .mapToDouble(ActivityLevel::getTdeeMultiplier)
                        .max()
                        .orElse(0);
        assertEquals(ActivityLevel.EXTRA_ACTIVE.getTdeeMultiplier(), max);
    }
}

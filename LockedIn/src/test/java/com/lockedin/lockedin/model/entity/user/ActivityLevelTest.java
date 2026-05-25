package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActivityLevelTest {

    @Test
    void fromDisplayName_returnsMatchingLevel() {
        assertEquals(ActivityLevel.SEDENTARY, ActivityLevel.fromDisplayName("Sedentary (little/no exercise)"));
    }

    @Test
    void fromDisplayName_returnsNull_forUnknownLabel() {
        assertNull(ActivityLevel.fromDisplayName("Unknown"));
    }

    @Test
    void fromDisplayName_returnsNull_forNullInput() {
        assertNull(ActivityLevel.fromDisplayName(null));
    }

    @Test
    void getTdeeMultiplier_increasesWithActivity() {
        assertTrue(ActivityLevel.LIGHTLY_ACTIVE.getTdeeMultiplier() > ActivityLevel.SEDENTARY.getTdeeMultiplier());
        assertTrue(
                ActivityLevel.VERY_ACTIVE.getTdeeMultiplier()
                        > ActivityLevel.MODERATELY_ACTIVE.getTdeeMultiplier());
    }

    @Test
    void toString_returnsDisplayName() {
        assertEquals("Very active (6–7 days/week)", ActivityLevel.VERY_ACTIVE.toString());
    }

    @Test
    void values_containsFiveLevels() {
        assertEquals(5, ActivityLevel.values().length);
    }

    @Test
    void getDisplayName_isUniqueAcrossValues() {
        long distinct =
                java.util.Arrays.stream(ActivityLevel.values())
                        .map(ActivityLevel::getDisplayName)
                        .distinct()
                        .count();
        assertEquals(ActivityLevel.values().length, distinct);
    }

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

package com.lockedin.lockedin.logic;

/**
 * Static helper for computing capped progress percentages.
 * @author LockedIn Team
 * @version 1.0
 */
public class ProgressCalculator {

    private ProgressCalculator() {}

    /**
     * Returns (actual / target) * 100, capped at 100.0.
     * Returns 0.0 when target is zero to avoid division by zero.
     * @param actual The current value.
     * @param target The goal value.
     * @return Percentage in the range [0.0, 100.0].
     */
    public static double calculatePercentage(double actual, double target) {
        if (target == 0.0) return 0.0;
        return Math.min(100.0, (actual / target) * 100.0);
    }
}

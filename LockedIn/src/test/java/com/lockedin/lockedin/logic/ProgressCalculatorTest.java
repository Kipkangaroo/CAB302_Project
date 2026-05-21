package com.lockedin.lockedin.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for ProgressCalculator (static percentage helper).
 */
public class ProgressCalculatorTest {

    @Test
    public void testCalculatePercentage_typical() {
        assertEquals(50.0, ProgressCalculator.calculatePercentage(50, 100), 0.0);
    }

    @Test
    public void testCalculatePercentage_atTarget() {
        assertEquals(100.0, ProgressCalculator.calculatePercentage(100, 100), 0.0);
    }

    @Test
    public void testCalculatePercentage_cappedAtHundred() {
        assertEquals(100.0, ProgressCalculator.calculatePercentage(150, 100), 0.0);
    }

    @Test
    public void testCalculatePercentage_zero() {
        assertEquals(0.0, ProgressCalculator.calculatePercentage(0, 100), 0.0);
    }

    @Test
    public void testCalculatePercentage_zeroTarget_returnsZero() {
        assertEquals(0.0, ProgressCalculator.calculatePercentage(50, 0), 0.0);
    }
}

package com.lockedin.lockedin.logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DietLogic}, covering macro addition and numeric input validation.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class DietLogicTest {
    private DietLogic logic;

    /**
     * Creates a fresh {@link DietLogic} instance before each test.
     */
    @BeforeEach
    public void setUp() {
        logic = new DietLogic();
    }


    /**
     * Verifies Add: Positive Numbers.
     */
    @Test
    public void testAdd_PositiveNumbers() {
        assertEquals(150, logic.add(100, 50));
    }


    /**
     * Verifies Add: Zero Values.
     */
    @Test
    public void testAdd_ZeroValues() {
        assertEquals(0, logic.add(0, 0));
    }


    /**
     * Verifies Add: Mixed Values.
     */
    @Test
    public void testAdd_MixedValues() {
        assertEquals(10, logic.add(5, 5));
    }


    /**
     * Verifies IsValidNumber: valid.
     */
    @Test
    public void testIsValidNumber_valid() {
        assertTrue(logic.isValidNumber("100"));
        assertTrue(logic.isValidNumber("0"));
    }


    /**
     * Verifies IsValidNumber: invalid Negative.
     */
    @Test
    public void testIsValidNumber_invalidNegative() {
        assertFalse(logic.isValidNumber("-1"));
    }


    /**
     * Verifies IsValidNumber: invalid Non Numeric.
     */
    @Test
    public void testIsValidNumber_invalidNonNumeric() {
        assertFalse(logic.isValidNumber("abc"));
    }


    /**
     * Verifies IsValidNumber: Empty Or Null.
     */
    @Test
    public void testIsValidNumber_EmptyOrNull() {
        assertFalse(logic.isValidNumber(" "));
        assertFalse(logic.isValidNumber(null));
    }
}

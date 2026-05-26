package com.lockedin.lockedin.controller.diet;

import static org.junit.jupiter.api.Assertions.*;

import com.lockedin.lockedin.logic.DietLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Tests the input validation logic exercised by DietController's add-food flow
/**
 * Unit tests for DietControllerInput, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class DietControllerInputTest {
    private DietLogic logic;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() {
        logic = new DietLogic();
    }


    /**
     * Verifies isValidNumber: accepts Large Calorie Value.
     */
    @Test
    void isValidNumber_acceptsLargeCalorieValue() {
        assertTrue(logic.isValidNumber("3500"));
    }


    /**
     * Verifies isValidNumber: accepts Typical Protein Value.
     */
    @Test
    void isValidNumber_acceptsTypicalProteinValue() {
        assertTrue(logic.isValidNumber("150"));
    }


    /**
     * Verifies isValidNumber: rejects Negative Decimal.
     */
    @Test
    void isValidNumber_rejectsNegativeDecimal() {
        assertFalse(logic.isValidNumber("-0.5"));
    }


    /**
     * Verifies isValidNumber: rejects Input With Embedded Letters.
     */
    @Test
    void isValidNumber_rejectsInputWithEmbeddedLetters() {
        assertFalse(logic.isValidNumber("50g"));
    }


    /**
     * Verifies isValidNumber: rejects Empty String.
     */
    @Test
    void isValidNumber_rejectsEmptyString() {
        assertFalse(logic.isValidNumber(""));
    }


    /**
     * Verifies isValidNumber: rejects Alphanumeric Mix.
     */
    @Test
    void isValidNumber_rejectsAlphanumericMix() {
        assertFalse(logic.isValidNumber("100kcal"));
    }


    /**
     * Verifies isValidNumber: rejects Null.
     */
    @Test
    void isValidNumber_rejectsNull() {
        assertFalse(logic.isValidNumber(null));
    }


    /**
     * Verifies add: correctly Sums Daily Calories.
     */
    @Test
    void add_correctlySumsDailyCalories() {
        assertEquals(2500, logic.add(2000, 500));
    }


    /**
     * Verifies add: zero Carbs returns Other Operand.
     */
    @Test
    void add_zeroCarbs_returnsOtherOperand() {
        assertEquals(75, logic.add(0, 75));
    }
}

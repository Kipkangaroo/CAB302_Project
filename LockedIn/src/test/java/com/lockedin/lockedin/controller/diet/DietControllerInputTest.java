package com.lockedin.lockedin.controller.diet;

import static org.junit.jupiter.api.Assertions.*;

import com.lockedin.lockedin.logic.DietLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Tests the input validation logic exercised by DietController's add-food flow
public class DietControllerInputTest {
    private DietLogic logic;

    @BeforeEach
    void setUp() {
        logic = new DietLogic();
    }

    @Test
    void isValidNumber_acceptsLargeCalorieValue() {
        assertTrue(logic.isValidNumber("3500"));
    }

    @Test
    void isValidNumber_acceptsTypicalProteinValue() {
        assertTrue(logic.isValidNumber("150"));
    }

    @Test
    void isValidNumber_rejectsNegativeDecimal() {
        assertFalse(logic.isValidNumber("-0.5"));
    }

    @Test
    void isValidNumber_rejectsInputWithEmbeddedLetters() {
        assertFalse(logic.isValidNumber("50g"));
    }

    @Test
    void isValidNumber_rejectsEmptyString() {
        assertFalse(logic.isValidNumber(""));
    }

    @Test
    void isValidNumber_rejectsAlphanumericMix() {
        assertFalse(logic.isValidNumber("100kcal"));
    }

    @Test
    void isValidNumber_rejectsNull() {
        assertFalse(logic.isValidNumber(null));
    }

    @Test
    void add_correctlySumsDailyCalories() {
        assertEquals(2500, logic.add(2000, 500));
    }

    @Test
    void add_zeroCarbs_returnsOtherOperand() {
        assertEquals(75, logic.add(0, 75));
    }
}

package com.lockedin.lockedin.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DietLogicTest {
    private DietLogic logic;

    @BeforeEach
    public void setUp() {
        logic = new DietLogic();
    }

    // -------- Adding Values -------

    @Test
    public void testAdd_PositiveNumbers(){
        assertEquals(150, logic.add(100,50));
    }

    @Test
    public void testAdd_ZeroValues(){
        assertEquals(0,logic.add(0,0));
    }
    @Test
    public void testAdd_MixedValues(){
        assertEquals(10,logic.add(5,5));
    }

    // ------ Validation Tests -------

    @Test
    public void testIsValidNumber_valid(){
        assertTrue(logic.isValidNumber("100"));
        assertTrue(logic.isValidNumber("0"));
    }

    @Test
    public void testIsValidNumber_invalidNegative(){
        assertFalse(logic.isValidNumber("-1"));
    }

    @Test
    public void testIsValidNumber_invalidNonNumeric(){
        assertFalse(logic.isValidNumber("abc"));
    }

    @Test
    public void testIsValidNumber_EmptyOrNull(){
        assertFalse(logic.isValidNumber(" "));
        assertFalse(logic.isValidNumber(null));
    }
}

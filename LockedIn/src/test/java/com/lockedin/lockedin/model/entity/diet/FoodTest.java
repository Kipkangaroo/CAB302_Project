package com.lockedin.lockedin.model.entity.diet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * Unit tests for Food, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class FoodTest {


    /**
     * Verifies defaultConstructor: sets Today As Date.
     */
    @Test
    void defaultConstructor_setsTodayAsDate() {
        assertEquals(LocalDate.now(), new Food().getDate());
    }


    /**
     * Verifies parameterizedConstructor: sets Fields.
     */
    @Test
    void parameterizedConstructor_setsFields() {
        Food food = new Food(1, 5, "Oats", 300, 10, 50, 6);
        assertEquals(1, food.getId());
        assertEquals(5, food.getUserId());
        assertEquals("Oats", food.getName());
        assertEquals(300, food.getCalories());
        assertEquals(10, food.getProtein());
        assertEquals(50, food.getCarbs());
        assertEquals(6, food.getFats());
    }


    /**
     * Verifies setName: updates Value.
     */
    @Test
    void setName_updatesValue() {
        Food food = new Food();
        food.setName("Rice");
        assertEquals("Rice", food.getName());
    }


    /**
     * Verifies setMacros: updates All Macro Fields.
     */
    @Test
    void setMacros_updatesAllMacroFields() {
        Food food = new Food();
        food.setCalories(400);
        food.setProtein(20);
        food.setCarbs(45);
        food.setFats(12);
        assertEquals(400, food.getCalories());
        assertEquals(20, food.getProtein());
        assertEquals(45, food.getCarbs());
        assertEquals(12, food.getFats());
    }


    /**
     * Verifies setDate: updates Value.
     */
    @Test
    void setDate_updatesValue() {
        Food food = new Food();
        LocalDate date = LocalDate.of(2025, 12, 1);
        food.setDate(date);
        assertEquals(date, food.getDate());
    }


    /**
     * Verifies setUserId: updates Value.
     */
    @Test
    void setUserId_updatesValue() {
        Food food = new Food();
        food.setUserId(42);
        assertEquals(42, food.getUserId());
    }


    /**
     * Verifies setId: updates Value.
     */
    @Test
    void setId_updatesValue() {
        Food food = new Food();
        food.setId(99);
        assertEquals(99, food.getId());
    }


    /**
     * Verifies toString: includes Macros Summary.
     */
    @Test
    void toString_includesMacrosSummary() {
        Food food = new Food(0, 1, "Chicken", 250, 30, 0, 8);
        String text = food.toString();
        assertTrue(text.contains("Chicken"));
        assertTrue(text.contains("250"));
        assertTrue(text.contains("30"));
    }
}

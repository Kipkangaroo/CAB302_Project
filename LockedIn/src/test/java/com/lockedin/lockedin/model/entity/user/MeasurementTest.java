package com.lockedin.lockedin.model.entity.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Measurement, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class MeasurementTest {

    private Measurement mFull;
    private Measurement mNoId;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() {
        mFull = new Measurement(
                10,        // id
                999,       // userId
                72.5,      // value
                "Weight",  // type
                LocalDate.of(2024, 5, 20)
        );

        mNoId = new Measurement(
                999,       // userId
                65.0,      // value
                "BodyFat", // type
                LocalDate.of(2024, 5, 21)
        );
    }


    /**
     * Verifies test Constructor With Id.
     */
    @Test
    void testConstructorWithId() {
        assertEquals(10, mFull.getId());
        assertEquals(999, mFull.getUserId());
        assertEquals(72.5, mFull.getValue());
        assertEquals("Weight", mFull.getType());
        assertEquals(LocalDate.of(2024, 5, 20), mFull.getDate());
    }


    /**
     * Verifies test Constructor Without Id.
     */
    @Test
    void testConstructorWithoutId() {
        assertEquals(-1, mNoId.getId()); // default from your constructor
        assertEquals(999, mNoId.getUserId());
        assertEquals(65.0, mNoId.getValue());
        assertEquals("BodyFat", mNoId.getType());
        assertEquals(LocalDate.of(2024, 5, 21), mNoId.getDate());
    }


    /**
     * Verifies test Set Value.
     */
    @Test
    void testSetValue() {
        mFull.setValue(80.0);
        assertEquals(80.0, mFull.getValue());
    }


    /**
     * Verifies test Set Type.
     */
    @Test
    void testSetType() {
        mFull.setType("Waist");
        assertEquals("Waist", mFull.getType());
    }


    /**
     * Verifies test Set Date.
     */
    @Test
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2024, 6, 1);
        mFull.setDate(newDate);
        assertEquals(newDate, mFull.getDate());
    }


    /**
     * Verifies getUserId: returns Constructor Value.
     */
    @Test
    void getUserId_returnsConstructorValue() {
        assertEquals(999, mFull.getUserId());
    }


    /**
     * Verifies getId: returns Constructor Value.
     */
    @Test
    void getId_returnsConstructorValue() {
        assertEquals(10, mFull.getId());
    }


    /**
     * Verifies setValue: accepts Zero.
     */
    @Test
    void setValue_acceptsZero() {
        mNoId.setValue(0.0);
        assertEquals(0.0, mNoId.getValue());
    }
}

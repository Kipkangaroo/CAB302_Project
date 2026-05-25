package com.lockedin.lockedin.model.entity.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementTest {

    private Measurement mFull;
    private Measurement mNoId;

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

    @Test
    void testConstructorWithId() {
        assertEquals(10, mFull.getId());
        assertEquals(999, mFull.getUserId());
        assertEquals(72.5, mFull.getValue());
        assertEquals("Weight", mFull.getType());
        assertEquals(LocalDate.of(2024, 5, 20), mFull.getDate());
    }

    @Test
    void testConstructorWithoutId() {
        assertEquals(-1, mNoId.getId()); // default from your constructor
        assertEquals(999, mNoId.getUserId());
        assertEquals(65.0, mNoId.getValue());
        assertEquals("BodyFat", mNoId.getType());
        assertEquals(LocalDate.of(2024, 5, 21), mNoId.getDate());
    }

    @Test
    void testSetValue() {
        mFull.setValue(80.0);
        assertEquals(80.0, mFull.getValue());
    }

    @Test
    void testSetType() {
        mFull.setType("Waist");
        assertEquals("Waist", mFull.getType());
    }

    @Test
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2024, 6, 1);
        mFull.setDate(newDate);
        assertEquals(newDate, mFull.getDate());
    }

    @Test
    void getUserId_returnsConstructorValue() {
        assertEquals(999, mFull.getUserId());
    }

    @Test
    void getId_returnsConstructorValue() {
        assertEquals(10, mFull.getId());
    }

    @Test
    void setValue_acceptsZero() {
        mNoId.setValue(0.0);
        assertEquals(0.0, mNoId.getValue());
    }
}

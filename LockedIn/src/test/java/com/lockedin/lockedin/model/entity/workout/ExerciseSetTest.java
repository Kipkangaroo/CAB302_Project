package com.lockedin.lockedin.model.entity.workout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ExerciseSet, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class ExerciseSetTest {

    private Exercise benchPress;
    private ExerciseSet set;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() {
        benchPress = new Exercise(1, "Bench Press", "Press", "Strength", "Chest", "bench");
        set = new ExerciseSet(benchPress, 8, 60);
    }


    /**
     * Verifies fullConstructor: sets Reps And Weight.
     */
    @Test
    void fullConstructor_setsRepsAndWeight() {
        assertEquals(8, set.getReps());
        assertEquals(60, set.getWeight());
    }


    /**
     * Verifies getExercise: returns Linked Exercise.
     */
    @Test
    void getExercise_returnsLinkedExercise() {
        assertEquals("Bench Press", set.getExercise().getName());
    }


    /**
     * Verifies exerciseOnlyConstructor: leaves Defaults.
     */
    @Test
    void exerciseOnlyConstructor_leavesDefaults() {
        ExerciseSet partial = new ExerciseSet(benchPress);
        assertEquals(benchPress, partial.getExercise());
        assertEquals(0, partial.getReps());
        assertEquals(0.0, partial.getWeight());
    }


    /**
     * Verifies setReps: updates Value.
     */
    @Test
    void setReps_updatesValue() {
        set.setReps(10);
        assertEquals(10, set.getReps());
    }


    /**
     * Verifies setWeight: updates Value.
     */
    @Test
    void setWeight_updatesValue() {
        set.setWeight(80.5);
        assertEquals(80.5, set.getWeight());
    }


    /**
     * Verifies setExercise: updates Value.
     */
    @Test
    void setExercise_updatesValue() {
        Exercise row = new Exercise(2, "Row", "Pull", "Strength", "Back", "row");
        set.setExercise(row);
        assertEquals("Row", set.getExercise().getName());
    }


    /**
     * Verifies setReps: accepts Zero.
     */
    @Test
    void setReps_acceptsZero() {
        set.setReps(0);
        assertEquals(0, set.getReps());
    }


    /**
     * Verifies setWeight: accepts Fractional Load.
     */
    @Test
    void setWeight_acceptsFractionalLoad() {
        set.setWeight(52.5);
        assertEquals(52.5, set.getWeight(), 0.001);
    }
}

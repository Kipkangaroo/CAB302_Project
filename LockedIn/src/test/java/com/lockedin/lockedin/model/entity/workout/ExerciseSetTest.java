package com.lockedin.lockedin.model.entity.workout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExerciseSetTest {

    private Exercise benchPress;
    private ExerciseSet set;

    @BeforeEach
    void setUp() {
        benchPress = new Exercise(1, "Bench Press", "Press", "Strength", "Chest", "bench");
        set = new ExerciseSet(benchPress, 8, 60);
    }

    @Test
    void fullConstructor_setsRepsAndWeight() {
        assertEquals(8, set.getReps());
        assertEquals(60, set.getWeight());
    }

    @Test
    void getExercise_returnsLinkedExercise() {
        assertEquals("Bench Press", set.getExercise().getName());
    }

    @Test
    void exerciseOnlyConstructor_leavesDefaults() {
        ExerciseSet partial = new ExerciseSet(benchPress);
        assertEquals(benchPress, partial.getExercise());
        assertEquals(0, partial.getReps());
        assertEquals(0.0, partial.getWeight());
    }

    @Test
    void setReps_updatesValue() {
        set.setReps(10);
        assertEquals(10, set.getReps());
    }

    @Test
    void setWeight_updatesValue() {
        set.setWeight(80.5);
        assertEquals(80.5, set.getWeight());
    }

    @Test
    void setExercise_updatesValue() {
        Exercise row = new Exercise(2, "Row", "Pull", "Strength", "Back", "row");
        set.setExercise(row);
        assertEquals("Row", set.getExercise().getName());
    }

    @Test
    void setReps_acceptsZero() {
        set.setReps(0);
        assertEquals(0, set.getReps());
    }

    @Test
    void setWeight_acceptsFractionalLoad() {
        set.setWeight(52.5);
        assertEquals(52.5, set.getWeight(), 0.001);
    }
}

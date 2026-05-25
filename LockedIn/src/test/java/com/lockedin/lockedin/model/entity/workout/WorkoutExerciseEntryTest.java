package com.lockedin.lockedin.model.entity.workout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WorkoutExerciseEntryTest {

    @Test
    void shortConstructor_defaultsIdAndRest() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(4, "Bench Press", 5, 8);
        assertEquals(0, entry.getId());
        assertEquals(60, entry.getRestSeconds());
    }

    @Test
    void fullConstructor_setsAllFields() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(2, 4, "Deadlift", 3, 5, 120);
        assertEquals(2, entry.getId());
        assertEquals(4, entry.getExerciseId());
        assertEquals("Deadlift", entry.getExerciseName());
        assertEquals(3, entry.getSets());
        assertEquals(5, entry.getReps());
        assertEquals(120, entry.getRestSeconds());
    }

    @Test
    void toString_formatsExerciseSummary() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(1, 2, "Squat", 4, 6, 90);
        assertEquals("Squat  \u2013  4 sets \u00d7 6 reps", entry.toString());
    }

    @Test
    void getExerciseId_returnsValue() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(10, "Row", 3, 12);
        assertEquals(10, entry.getExerciseId());
    }

    @Test
    void getExerciseName_returnsValue() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(11, "Pull Up", 3, 8);
        assertEquals("Pull Up", entry.getExerciseName());
    }

    @Test
    void getSets_returnsValue() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(12, "Curl", 2, 15);
        assertEquals(2, entry.getSets());
    }

    @Test
    void getReps_returnsValue() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(13, "Extension", 3, 10);
        assertEquals(10, entry.getReps());
    }

    @Test
    void getRestSeconds_returnsValueFromFullConstructor() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(1, 14, "Press", 5, 5, 45);
        assertEquals(45, entry.getRestSeconds());
    }
}

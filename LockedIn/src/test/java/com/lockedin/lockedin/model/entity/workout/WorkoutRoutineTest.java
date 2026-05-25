package com.lockedin.lockedin.model.entity.workout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class WorkoutRoutineTest {

    private WorkoutRoutine routine;
    private Exercise squat;

    @BeforeEach
    void setUp() {
        routine = new WorkoutRoutine();
        squat = new Exercise(1, "Squat", "Descend", "Strength", "Legs", "squat");
        routine.getExercises(); // ensure map initialized via default constructor
    }

    @Test
    void defaultConstructor_startsWithNoExercises() {
        assertTrue(new WorkoutRoutine().getExercises().isEmpty());
    }

    @Test
    void setAndGetName() {
        routine.setName("Leg Day");
        assertEquals("Leg Day", routine.getName());
    }

    @Test
    void setAndGetDuration() {
        routine.setDuration("45 min");
        assertEquals("45 min", routine.getDuration());
    }

    @Test
    void setAndGetIntensity() {
        routine.setIntensity("High");
        assertEquals("High", routine.getIntensity());
    }

    @Test
    void setAndGetNotes() {
        routine.setNotes("Focus on form");
        assertEquals("Focus on form", routine.getNotes());
    }

    @Test
    void setAndGetId() {
        routine.setId(9);
        assertEquals(9, routine.getId());
    }

    @Test
    void getTotalVolume_returnsZero_whenEmpty() {
        assertEquals(0, routine.getTotalVolume());
    }

    @Test
    void trackSetsAndVolume_withExerciseTracking() throws Exception {
        var mapField = WorkoutRoutine.class.getDeclaredField("exerciseTracking");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var tracking =
                (java.util.HashMap<Exercise, ArrayList<ExerciseSet>>) mapField.get(routine);
        tracking.put(squat, new ArrayList<>());
        tracking.get(squat).add(new ExerciseSet(squat, 5, 100));
        tracking.get(squat).add(new ExerciseSet(squat, 5, 80));
        assertEquals(2, routine.getTotalSets(squat));
        assertEquals(900, routine.getTotalVolume());
    }
}

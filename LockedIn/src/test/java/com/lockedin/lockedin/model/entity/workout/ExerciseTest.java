package com.lockedin.lockedin.model.entity.workout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExerciseTest {

    @Test
    void getExerciseImageUrl_buildsExpectedPath() {
        Exercise exercise =
                new Exercise(1, "Push Up", "Keep body straight", "Strength", "Chest", "push-up");
        assertEquals(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/push-up/0.jpg",
                exercise.getExerciseImageUrl(0));
    }

    @Test
    void getExerciseImageUrl_usesOptionIndex() {
        Exercise exercise =
                new Exercise(2, "Squat", "Descend", "Strength", "Legs", "squat");
        assertTrue(exercise.getExerciseImageUrl(1).endsWith("/squat/1.jpg"));
    }

    @Test
    void toString_returnsExerciseName() {
        Exercise exercise = new Exercise(3, "Row", "Pull", "Strength", "Back", "row");
        assertEquals("Row", exercise.toString());
    }

    @Test
    void setId_updatesValue() {
        Exercise exercise = new Exercise(0, "Curl", "Curl up", "Strength", "Arms", "curl");
        exercise.setId(15);
        assertEquals(15, exercise.getId());
    }

    @Test
    void getters_returnConstructorValues() {
        Exercise exercise = new Exercise(4, "Plank", "Hold", "Core", "Abs", "plank");
        assertEquals("Plank", exercise.getName());
        assertEquals("Hold", exercise.getInstruction());
        assertEquals("Core", exercise.getCategory());
        assertEquals("Abs", exercise.getPrimaryMuscle());
        assertEquals("plank", exercise.getExerciseImageId());
    }

    @Test
    void setName_updatesValue() {
        Exercise exercise = new Exercise(5, "Old", "x", "Strength", "Legs", "old");
        exercise.setName("New");
        assertEquals("New", exercise.getName());
    }

    @Test
    void setInstruction_updatesValue() {
        Exercise exercise = new Exercise(6, "Lunge", "old", "Strength", "Legs", "lunge");
        exercise.setInstruction("Step forward");
        assertEquals("Step forward", exercise.getInstruction());
    }

    @Test
    void setExerciseImageId_updatesValue() {
        Exercise exercise = new Exercise(7, "Press", "Press up", "Strength", "Chest", "old");
        exercise.setExerciseImageId("bench");
        assertEquals("bench", exercise.getExerciseImageId());
    }
}

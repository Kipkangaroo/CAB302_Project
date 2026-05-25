package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.lockedin.lockedin.model.entity.workout.Exercise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

class ExercisesDAOTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private ExercisesDAO exercisesDAO;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        exercisesDAO = new ExercisesDAO(conn);
    }

    private Exercise sampleExercise(String name) {
        return new Exercise(0, name, "Instruction", "Strength", "Chest", name.toLowerCase());
    }

    @Test
    void addExercise_assignsGeneratedId() {
        Exercise exercise = sampleExercise("Push Up");
        exercisesDAO.addExercise(exercise);
        assertTrue(exercise.getId() > 0);
    }

    @Test
    void getExerciseById_returnsExercise_whenExists() {
        Exercise exercise = sampleExercise("Squat");
        exercisesDAO.addExercise(exercise);
        Exercise found = exercisesDAO.getExerciseById(exercise.getId());
        assertNotNull(found);
        assertEquals("Squat", found.getName());
    }

    @Test
    void getExerciseById_returnsNull_whenMissing() {
        assertNull(exercisesDAO.getExerciseById(99999));
    }

    @Test
    void getAllExercises_returnsInsertedRows() {
        exercisesDAO.addExercise(sampleExercise("Row"));
        exercisesDAO.addExercise(sampleExercise("Curl"));
        assertEquals(2, exercisesDAO.getAllExercises().size());
    }

    @Test
    void getAllExercises_returnsEmpty_whenTableEmpty() {
        assertTrue(exercisesDAO.getAllExercises().isEmpty());
    }

    @Test
    void findExerciseIdByName_returnsId_caseInsensitive() {
        Exercise exercise = sampleExercise("Deadlift");
        exercisesDAO.addExercise(exercise);
        assertEquals(exercise.getId(), exercisesDAO.findExerciseIdByName("deadlift"));
    }

    @Test
    void findExerciseIdByName_returnsZero_whenNotFound() {
        assertEquals(0, exercisesDAO.findExerciseIdByName("Unknown Exercise"));
    }

    @Test
    void getAllExercises_ordersByName() {
        exercisesDAO.addExercise(sampleExercise("Zercher"));
        exercisesDAO.addExercise(sampleExercise("Arnold Press"));
        assertEquals("Arnold Press", exercisesDAO.getAllExercises().get(0).getName());
        assertEquals("Zercher", exercisesDAO.getAllExercises().get(1).getName());
    }
}

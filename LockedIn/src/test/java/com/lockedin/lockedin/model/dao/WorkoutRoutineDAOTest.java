package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.WorkoutExerciseEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for WorkoutRoutineDAO using an isolated in‑memory SQLite database.
 * Verifies routine creation, retrieval, deletion, and exercise‑level operations.
 */
public class WorkoutRoutineDAOTest {
    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private static final int USER_ID = 1;
    private WorkoutRoutineDAO workoutDAO;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        workoutDAO = new WorkoutRoutineDAO(conn);
    }

    private List<WorkoutExerciseEntry> twoExercises() {
        return List.of(
                new WorkoutExerciseEntry(1, "Squat", 3, 10),
                new WorkoutExerciseEntry(2, "Bench Press", 4, 8)
        );
    }

    @Test
    void saveRoutine_returnsPositiveId() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", "notes", new ArrayList<>());
        assertTrue(id > 0);
    }

    @Test
    void getRoutinesByUser_returnsSavedRoutine() {
        workoutDAO.saveRoutine(USER_ID, "Leg Day", null, new ArrayList<>());
        List<WorkoutRoutineDAO.RoutineData> routines = workoutDAO.getRoutinesByUser(USER_ID);
        assertEquals(1, routines.size());
        assertEquals("Leg Day", routines.get(0).name);
    }

    @Test
    void getRoutinesByUser_returnsEmpty_forDifferentUser() {
        workoutDAO.saveRoutine(USER_ID, "Push Day", null, new ArrayList<>());
        List<WorkoutRoutineDAO.RoutineData> routines = workoutDAO.getRoutinesByUser(99);
        assertTrue(routines.isEmpty());
    }

    @Test
    void getRoutineById_returnsRoutine() {
        int id = workoutDAO.saveRoutine(USER_ID, "Pull Day", "back + biceps", new ArrayList<>());
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(id);
        assertNotNull(routine);
        assertEquals("Pull Day", routine.name);
        assertEquals("back + biceps", routine.notes);
    }

    @Test
    void getRoutineById_returnsNull_whenNotFound() {
        assertNull(workoutDAO.getRoutineById(999));
    }

    @Test
    void deleteRoutine_removesRoutine() {
        int id = workoutDAO.saveRoutine(USER_ID, "Temp", null, new ArrayList<>());
        workoutDAO.deleteRoutine(id);
        assertNull(workoutDAO.getRoutineById(id));
    }

    @Test
    void saveRoutine_storesExercises() {
        int id = workoutDAO.saveRoutine(USER_ID, "Full Body", null, twoExercises());
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(id);
        assertEquals(2, routine.exercises.size());
    }

    @Test
    void addExerciseToRoutine_addsExercise() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", null, new ArrayList<>());
        workoutDAO.addExerciseToRoutine(id, 3, "Overhead Press", 3, 12, 60);
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(id);
        assertEquals(1, routine.exercises.size());
        assertEquals("Overhead Press", routine.exercises.get(0).getExerciseName());
    }

    @Test
    void removeExerciseFromRoutine_removesExercise() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", null, twoExercises());
        WorkoutRoutineDAO.RoutineData before = workoutDAO.getRoutineById(id);
        int entryId = before.exercises.get(0).getId();
        workoutDAO.removeExerciseFromRoutine(entryId);
        WorkoutRoutineDAO.RoutineData after = workoutDAO.getRoutineById(id);
        assertEquals(1, after.exercises.size());
    }

    @Test
    void updateExercise_updatesValues() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", null, twoExercises());
        WorkoutRoutineDAO.RoutineData before = workoutDAO.getRoutineById(id);
        int entryId = before.exercises.get(0).getId();
        workoutDAO.updateExercise(entryId, 5, 6, 90);
        WorkoutRoutineDAO.RoutineData after = workoutDAO.getRoutineById(id);
        WorkoutExerciseEntry updated = after.exercises.stream()
                .filter(e -> e.getId() == entryId).findFirst().orElseThrow();
        assertEquals(5, updated.getSets());
        assertEquals(6, updated.getReps());
        assertEquals(90, updated.getRestSeconds());
    }
}
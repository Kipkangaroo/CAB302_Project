package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for WorkoutRoutineDAO using an isolated in-memory SQLite database.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class WorkoutRoutineDAOTest {
    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private static final int USER_ID = 1;
    private WorkoutRoutineDAO workoutDAO;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        workoutDAO = new WorkoutRoutineDAO(conn);
    }

    /**
     * Returns a two-exercise list used when saving routines in tests.
     */
    private List<WorkoutExerciseEntry> twoExercises() {
        return List.of(
                new WorkoutExerciseEntry(1, "Squat", 3, 10),
                new WorkoutExerciseEntry(2, "Bench Press", 4, 8));
    }


    /**
     * Verifies saveRoutine: returns Positive Id.
     */
    @Test
    void saveRoutine_returnsPositiveId() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", "notes", new ArrayList<>());
        assertTrue(id > 0);
    }


    /**
     * Verifies getRoutinesByUser: returns Saved Routine.
     */
    @Test
    void getRoutinesByUser_returnsSavedRoutine() {
        workoutDAO.saveRoutine(USER_ID, "Leg Day", null, new ArrayList<>());
        List<WorkoutRoutineDAO.RoutineData> routines = workoutDAO.getRoutinesByUser(USER_ID);
        assertEquals(1, routines.size());
        assertEquals("Leg Day", routines.get(0).name);
    }


    /**
     * Verifies getRoutinesByUser: returns Empty for Different User.
     */
    @Test
    void getRoutinesByUser_returnsEmpty_forDifferentUser() {
        workoutDAO.saveRoutine(USER_ID, "Push Day", null, new ArrayList<>());
        List<WorkoutRoutineDAO.RoutineData> routines = workoutDAO.getRoutinesByUser(99);
        assertTrue(routines.isEmpty());
    }


    /**
     * Verifies getRoutineById: returns Routine.
     */
    @Test
    void getRoutineById_returnsRoutine() {
        int id = workoutDAO.saveRoutine(USER_ID, "Pull Day", "back + biceps", new ArrayList<>());
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(id);
        assertNotNull(routine);
        assertEquals("Pull Day", routine.name);
        assertEquals("back + biceps", routine.notes);
    }


    /**
     * Verifies getRoutineById: returns Null when Not Found.
     */
    @Test
    void getRoutineById_returnsNull_whenNotFound() {
        assertNull(workoutDAO.getRoutineById(999));
    }


    /**
     * Verifies deleteRoutine: removes Routine.
     */
    @Test
    void deleteRoutine_removesRoutine() {
        int id = workoutDAO.saveRoutine(USER_ID, "Temp", null, new ArrayList<>());
        workoutDAO.deleteRoutine(id);
        assertNull(workoutDAO.getRoutineById(id));
    }


    /**
     * Verifies saveRoutine: stores Exercises.
     */
    @Test
    void saveRoutine_storesExercises() {
        int id = workoutDAO.saveRoutine(USER_ID, "Full Body", null, twoExercises());
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(id);
        assertEquals(2, routine.exercises.size());
    }


    /**
     * Verifies addExerciseToRoutine: adds Exercise.
     */
    @Test
    void addExerciseToRoutine_addsExercise() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", null, new ArrayList<>());
        workoutDAO.addExerciseToRoutine(id, 3, "Overhead Press", 3, 12, 60);
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(id);
        assertEquals(1, routine.exercises.size());
        assertEquals("Overhead Press", routine.exercises.get(0).getExerciseName());
    }


    /**
     * Verifies removeExerciseFromRoutine: removes Exercise.
     */
    @Test
    void removeExerciseFromRoutine_removesExercise() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", null, twoExercises());
        WorkoutRoutineDAO.RoutineData before = workoutDAO.getRoutineById(id);
        int entryId = before.exercises.get(0).getId();
        workoutDAO.removeExerciseFromRoutine(entryId);
        WorkoutRoutineDAO.RoutineData after = workoutDAO.getRoutineById(id);
        assertEquals(1, after.exercises.size());
    }


    /**
     * Verifies getCompletedWorkoutsByUserBetween: returns Only Workouts In Range.
     */
    @Test
    void getCompletedWorkoutsByUserBetween_returnsOnlyWorkoutsInRange() {
        int routineId = workoutDAO.saveRoutine(USER_ID, "Range Test", null, new ArrayList<>());
        WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(routineId);

        LocalDateTime now = LocalDateTime.now();
        workoutDAO.saveCompletedWorkout(USER_ID, routine, List.of(), now);
        workoutDAO.saveCompletedWorkout(USER_ID, routine, List.of(), now.minusDays(1));
        workoutDAO.saveCompletedWorkout(USER_ID, routine, List.of(), now.minusDays(10));

        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();
        List<WorkoutRoutineDAO.CompletedWorkoutData> results =
                workoutDAO.getCompletedWorkoutsByUserBetween(USER_ID, start, end);

        assertEquals(2, results.size());
        String tenDaysAgo = LocalDate.now().minusDays(10).toString();
        assertFalse(results.stream().anyMatch(w -> w.completedAt.startsWith(tenDaysAgo)));
    }


    /**
     * Verifies updateExercise: updates Values.
     */
    @Test
    void updateExercise_updatesValues() {
        int id = workoutDAO.saveRoutine(USER_ID, "Push Day", null, twoExercises());
        WorkoutRoutineDAO.RoutineData before = workoutDAO.getRoutineById(id);
        int entryId = before.exercises.get(0).getId();
        workoutDAO.updateExercise(entryId, 5, 6, 90);
        WorkoutRoutineDAO.RoutineData after = workoutDAO.getRoutineById(id);
        WorkoutExerciseEntry updated =
                after.exercises.stream()
                        .filter(e -> e.getId() == entryId)
                        .findFirst()
                        .orElseThrow();
        assertEquals(5, updated.getSets());
        assertEquals(6, updated.getReps());
        assertEquals(90, updated.getRestSeconds());
    }
}

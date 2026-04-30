package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.WorkoutExerciseEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutRoutineDAOTest {
    private WorkoutRoutineDAO dao;
    private static final int USER_ID = 1;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        dao = new WorkoutRoutineDAO(conn);
    }

    private List<WorkoutExerciseEntry> twoExercises() {
        return List.of(
                new WorkoutExerciseEntry(1, "Squat", 3, 10),
                new WorkoutExerciseEntry(2, "Bench Press", 4, 8)
        );
    }

    @Test
    void saveRoutine_returnsPositiveId() {
        int id = dao.saveRoutine(USER_ID, "Push Day", "notes", new ArrayList<>());
        assertTrue(id > 0);
    }

    @Test
    void getRoutinesByUser_returnsSavedRoutine() {
        dao.saveRoutine(USER_ID, "Leg Day", null, new ArrayList<>());
        List<WorkoutRoutineDAO.RoutineData> routines = dao.getRoutinesByUser(USER_ID);
        assertEquals(1, routines.size());
        assertEquals("Leg Day", routines.get(0).name);
    }

    @Test
    void getRoutinesByUser_returnsEmpty_forDifferentUser() {
        dao.saveRoutine(USER_ID, "Push Day", null, new ArrayList<>());
        List<WorkoutRoutineDAO.RoutineData> routines = dao.getRoutinesByUser(99);
        assertTrue(routines.isEmpty());
    }

    @Test
    void getRoutineById_returnsRoutine() {
        int id = dao.saveRoutine(USER_ID, "Pull Day", "back + biceps", new ArrayList<>());
        WorkoutRoutineDAO.RoutineData routine = dao.getRoutineById(id);
        assertNotNull(routine);
        assertEquals("Pull Day", routine.name);
        assertEquals("back + biceps", routine.notes);
    }

    @Test
    void getRoutineById_returnsNull_whenNotFound() {
        assertNull(dao.getRoutineById(999));
    }

    @Test
    void deleteRoutine_removesRoutine() {
        int id = dao.saveRoutine(USER_ID, "Temp", null, new ArrayList<>());
        dao.deleteRoutine(id);
        assertNull(dao.getRoutineById(id));
    }

    @Test
    void saveRoutine_storesExercises() {
        int id = dao.saveRoutine(USER_ID, "Full Body", null, twoExercises());
        WorkoutRoutineDAO.RoutineData routine = dao.getRoutineById(id);
        assertEquals(2, routine.exercises.size());
    }

    @Test
    void addExerciseToRoutine_addsExercise() {
        int id = dao.saveRoutine(USER_ID, "Push Day", null, new ArrayList<>());
        dao.addExerciseToRoutine(id, 3, "Overhead Press", 3, 12, 60);
        WorkoutRoutineDAO.RoutineData routine = dao.getRoutineById(id);
        assertEquals(1, routine.exercises.size());
        assertEquals("Overhead Press", routine.exercises.get(0).getExerciseName());
    }

    @Test
    void removeExerciseFromRoutine_removesExercise() {
        int id = dao.saveRoutine(USER_ID, "Push Day", null, twoExercises());
        WorkoutRoutineDAO.RoutineData before = dao.getRoutineById(id);
        int entryId = before.exercises.get(0).getId();
        dao.removeExerciseFromRoutine(entryId);
        WorkoutRoutineDAO.RoutineData after = dao.getRoutineById(id);
        assertEquals(1, after.exercises.size());
    }

    @Test
    void updateExercise_updatesValues() {
        int id = dao.saveRoutine(USER_ID, "Push Day", null, twoExercises());
        WorkoutRoutineDAO.RoutineData before = dao.getRoutineById(id);
        int entryId = before.exercises.get(0).getId();
        dao.updateExercise(entryId, 5, 6, 90);
        WorkoutRoutineDAO.RoutineData after = dao.getRoutineById(id);
        WorkoutExerciseEntry updated = after.exercises.stream()
                .filter(e -> e.getId() == entryId).findFirst().orElseThrow();
        assertEquals(5, updated.getSets());
        assertEquals(6, updated.getReps());
        assertEquals(90, updated.getRestSeconds());
    }
}
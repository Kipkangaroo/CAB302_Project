package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for workout routine and completion history records.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class WorkoutRoutineDAO {

    private final Connection connection;

    /**
     * Opens the default users database and ensures workout tables exist.
     */
    public WorkoutRoutineDAO() {
        connection = SqliteConnection.getInstance("users.db");
        createTables();
        migrate();
    }

    /**
     * Uses the supplied connection (for example an in-memory database in tests).
     *
     * @param connection active SQLite connection
     */
    public WorkoutRoutineDAO(Connection connection) {
        this.connection = connection;
        createTables();
        migrate();
    }

    /** Creates workout, exercise, and completion tables when missing. */
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    """
                                CREATE TABLE IF NOT EXISTS workout_routines (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    user_id INTEGER NOT NULL,
                                    name TEXT NOT NULL,
                                    notes TEXT DEFAULT '',
                                    created_at TEXT NOT NULL
                                )
                            """);
            stmt.execute(
                    """
                                CREATE TABLE IF NOT EXISTS routine_exercises (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    routine_id INTEGER NOT NULL,
                                    exercise_id INTEGER NOT NULL,
                                    exercise_name TEXT NOT NULL,
                                    sets INTEGER NOT NULL,
                                    reps INTEGER NOT NULL,
                                    rest_seconds INTEGER NOT NULL DEFAULT 60
                                )
                            """);
            stmt.execute(
                    """
                                CREATE TABLE IF NOT EXISTS completed_workouts (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    user_id INTEGER NOT NULL,
                                    routine_id INTEGER NOT NULL,
                                    routine_name TEXT NOT NULL,
                                    completed_at TEXT NOT NULL,
                                    total_exercises INTEGER NOT NULL,
                                    total_sets INTEGER NOT NULL
                                )
                            """);
            stmt.execute(
                    """
                                CREATE TABLE IF NOT EXISTS completed_workout_sets (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    completed_workout_id INTEGER NOT NULL,
                                    exercise_id INTEGER NOT NULL,
                                    exercise_name TEXT NOT NULL,
                                    set_number INTEGER NOT NULL,
                                    target_reps INTEGER NOT NULL,
                                    completed_reps INTEGER NOT NULL,
                                    rest_seconds INTEGER NOT NULL
                                )
                            """);
        } catch (SQLException e) {
            System.err.println("Error creating workout tables: " + e.getMessage());
        }
    }

    /**
     * Adds columns that may be missing in databases created by an older version.
     */
    private void migrate() {
        try (Statement stmt = connection.createStatement()) {
            try {
                stmt.execute("ALTER TABLE workout_routines ADD COLUMN notes TEXT DEFAULT ''");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute(
                        "ALTER TABLE routine_exercises ADD COLUMN rest_seconds INTEGER NOT NULL"
                                + " DEFAULT 60");
            } catch (SQLException ignored) {
            }
        } catch (SQLException e) {
            System.err.println("Migration error: " + e.getMessage());
        }
    }

    /**
     * Persists a new workout routine and its exercises for a user.
     *
     * @param userId owner of the routine
     * @param name display name shown in the UI
     * @param notes optional description (empty string when null)
     * @param entries exercises to store with the routine
     * @return generated routine id, or -1 on failure
     */
    public int saveRoutine(
            int userId, String name, String notes, List<WorkoutExerciseEntry> entries) {
        String sql = "INSERT INTO workout_routines (user_id, name, notes, created_at) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, notes == null ? "" : notes);
            ps.setString(4, LocalDate.now().toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int routineId = keys.getInt(1);
                insertExercises(routineId, entries);
                return routineId;
            }
        } catch (SQLException e) {
            System.err.println("Error saving routine: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Batch-inserts exercise rows for an existing routine.
     *
     * @param routineId parent routine id
     * @param entries exercise slots to persist
     */
    private void insertExercises(int routineId, List<WorkoutExerciseEntry> entries)
            throws SQLException {
        String sql = """
                    INSERT INTO routine_exercises
                        (routine_id, exercise_id, exercise_name, sets, reps, rest_seconds)
                    VALUES (?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (WorkoutExerciseEntry entry : entries) {
                ps.setInt(1, routineId);
                ps.setInt(2, entry.getExerciseId());
                ps.setString(3, entry.getExerciseName());
                ps.setInt(4, entry.getSets());
                ps.setInt(5, entry.getReps());
                ps.setInt(6, entry.getRestSeconds());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Loads all routines for a user, each with its exercise list.
     *
     * @param userId owner of the routines
     * @return routines ordered by creation date (newest first)
     */
    public List<RoutineData> getRoutinesByUser(int userId) {
        List<RoutineData> routines = new ArrayList<>();
        String sql = "SELECT id, name, notes FROM workout_routines WHERE user_id=? ORDER BY created_at"
                + " DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                routines.add(
                        new RoutineData(
                                id,
                                rs.getString("name"),
                                rs.getString("notes"),
                                getExercisesForRoutine(id)));
            }
        } catch (SQLException e) {
            System.err.println("Error loading routines: " + e.getMessage());
        }
        return routines;
    }

    // ── Save ────────────────────────────────────────────────────────────────

    /**
     * Counts completed workout sessions recorded for a user.
     *
     * @param userId owner of the history
     * @return number of rows in completed_workouts
     */
    public int getTotalWorkoutsByUser(int userId) {
        int totalWorkouts = 0;
        String sql = "SELECT COUNT(*) FROM completed_workouts WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalWorkouts = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error loading total workouts: " + e.getMessage());
        }
        return totalWorkouts;
    }

    /**
     * Returns the routine by id.
     * @param routineId The routine id.
     * @return routine by id
     */
    public RoutineData getRoutineById(int routineId) {
        String sql = "SELECT id, name, notes FROM workout_routines WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routineId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new RoutineData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("notes"),
                        getExercisesForRoutine(routineId));
            }
        } catch (SQLException e) {
            System.err.println("Error loading routine: " + e.getMessage());
        }
        return null;
    }

    // ── Read ────────────────────────────────────────────────────────────────

    /**
     * Returns the exercises for routine.
     * @param routineId The routine id.
     * @return exercises for routine
     * @throws SQLException If the operation fails.
     */
    private List<WorkoutExerciseEntry> getExercisesForRoutine(int routineId) throws SQLException {
        List<WorkoutExerciseEntry> entries = new ArrayList<>();
        String sql = "SELECT id, exercise_id, exercise_name, sets, reps, rest_seconds "
                + "FROM routine_exercises WHERE routine_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routineId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entries.add(
                        new WorkoutExerciseEntry(
                                rs.getInt("id"),
                                rs.getInt("exercise_id"),
                                rs.getString("exercise_name"),
                                rs.getInt("sets"),
                                rs.getInt("reps"),
                                rs.getInt("rest_seconds")));
            }
        }
        return entries;
    }

    /**
     * Update exercise.
     * @param entryId The entry id.
     * @param sets sets
     * @param reps reps
     * @param restSeconds The rest seconds.
     */
    public void updateExercise(int entryId, int sets, int reps, int restSeconds) {
        String sql = "UPDATE routine_exercises SET sets=?, reps=?, rest_seconds=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sets);
            ps.setInt(2, reps);
            ps.setInt(3, restSeconds);
            ps.setInt(4, entryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating exercise: " + e.getMessage());
        }
    }

    /**
     * Add exercise to routine.
     * @param routineId The routine id.
     * @param exerciseId The exercise id.
     * @param exerciseName The exercise name.
     * @param sets sets
     * @param reps reps
     * @param restSeconds The rest seconds.
     */
    public void addExerciseToRoutine(
            int routineId,
            int exerciseId,
            String exerciseName,
            int sets,
            int reps,
            int restSeconds) {
        String sql = """
                    INSERT INTO routine_exercises
                        (routine_id, exercise_id, exercise_name, sets, reps, rest_seconds)
                    VALUES (?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routineId);
            ps.setInt(2, exerciseId);
            ps.setString(3, exerciseName);
            ps.setInt(4, sets);
            ps.setInt(5, reps);
            ps.setInt(6, restSeconds);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding exercise: " + e.getMessage());
        }
    }

    /**
     * Remove exercise from routine.
     * @param entryId The entry id.
     */
    public void removeExerciseFromRoutine(int entryId) {
        String sql = "DELETE FROM routine_exercises WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing exercise: " + e.getMessage());
        }
    }

    // ── Update ──────────────────────────────────────────────────────────────

    /**
     * Delete routine.
     * @param routineId The routine id.
     */
    public void deleteRoutine(int routineId) {
        try (PreparedStatement ps1 = connection.prepareStatement(
                "DELETE FROM routine_exercises WHERE routine_id=?");
                PreparedStatement ps2 = connection.prepareStatement("DELETE FROM workout_routines WHERE id=?")) {
            ps1.setInt(1, routineId);
            ps1.executeUpdate();
            ps2.setInt(1, routineId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting routine: " + e.getMessage());
        }
    }

    /** Removes all routines and completed workout history for a user. */
    public void deleteAllForUser(int userId) {
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    """
                            DELETE FROM completed_workout_sets
                            WHERE completed_workout_id IN (
                                SELECT id FROM completed_workouts WHERE user_id = ?
                            )
                            """)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM completed_workouts WHERE user_id = ?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    """
                            DELETE FROM routine_exercises
                            WHERE routine_id IN (
                                SELECT id FROM workout_routines WHERE user_id = ?
                            )
                            """)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM workout_routines WHERE user_id = ?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete workout data for user " + userId, e);
        }
    }

    /**
     * Save completed workout.
     * @param userId The user id.
     * @param routine routine
     * @param completedSets The completed sets.
     * @return numeric result
     */
    public int saveCompletedWorkout(
            int userId, RoutineData routine, List<CompletedSetData> completedSets) {
        return saveCompletedWorkout(userId, routine, completedSets, LocalDateTime.now());
    }

    /**
     * Save completed workout.
     * @param userId The user id.
     * @param routine routine
     * @param completedSets The completed sets.
     * @param completedAt The completed at.
     * @return numeric result
     */
    public int saveCompletedWorkout(
            int userId,
            RoutineData routine,
            List<CompletedSetData> completedSets,
            LocalDateTime completedAt) {
        String sql = """
                    INSERT INTO completed_workouts
                        (user_id, routine_id, routine_name, completed_at, total_exercises, total_sets)
                    VALUES (?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, routine.id);
            ps.setString(3, routine.name);
            ps.setString(4, completedAt.toString());
            ps.setInt(5, routine.exercises.size());
            ps.setInt(6, completedSets.size());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int completedWorkoutId = keys.getInt(1);
                    insertCompletedSets(completedWorkoutId, completedSets);
                    return completedWorkoutId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving completed workout: " + e.getMessage());
        }
        return -1;
    }

    /**
     * All completed workouts for a user, newest first.
     */
    public List<CompletedWorkoutData> getCompletedWorkoutsByUser(int userId) {
        return getCompletedWorkoutsByUser(userId, null);
    }

    // ── Delete ──────────────────────────────────────────────────────────────

    /**
     * Completed workouts for a user on a specific calendar day
     * ({@code completed_at} is matched by
     * SQLite {@code date(completed_at)}), newest first within that day.
     */
    public List<CompletedWorkoutData> getCompletedWorkoutsByUser(int userId, LocalDate onDate) {
        List<CompletedWorkoutData> workouts = new ArrayList<>();
        String sql = onDate == null
                ? """
                        SELECT id, routine_id, routine_name, completed_at, total_exercises, total_sets
                        FROM completed_workouts
                        WHERE user_id=?
                        ORDER BY completed_at DESC
                        """
                : """
                        SELECT id, routine_id, routine_name, completed_at, total_exercises, total_sets
                        FROM completed_workouts
                        WHERE user_id=? AND date(completed_at)=?
                        ORDER BY completed_at DESC
                        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (onDate != null) {
                ps.setString(2, onDate.toString());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int completedWorkoutId = rs.getInt("id");
                    workouts.add(
                            new CompletedWorkoutData(
                                    completedWorkoutId,
                                    rs.getInt("routine_id"),
                                    rs.getString("routine_name"),
                                    rs.getString("completed_at"),
                                    rs.getInt("total_exercises"),
                                    rs.getInt("total_sets"),
                                    getCompletedSetsForWorkout(completedWorkoutId)));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading completed workouts: " + e.getMessage());
        }
        return workouts;
    }

    /**
     * Completed workouts for a user within a calendar date range (inclusive),
     * newest first.
     */
    public List<CompletedWorkoutData> getCompletedWorkoutsByUserBetween(
            int userId, LocalDate start, LocalDate end) {
        List<CompletedWorkoutData> workouts = new ArrayList<>();
        String sql = """
                SELECT id, routine_id, routine_name, completed_at, total_exercises, total_sets
                FROM completed_workouts
                WHERE user_id=? AND date(completed_at) BETWEEN ? AND ?
                ORDER BY completed_at DESC
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, start.toString());
            ps.setString(3, end.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int completedWorkoutId = rs.getInt("id");
                    workouts.add(
                            new CompletedWorkoutData(
                                    completedWorkoutId,
                                    rs.getInt("routine_id"),
                                    rs.getString("routine_name"),
                                    rs.getString("completed_at"),
                                    rs.getInt("total_exercises"),
                                    rs.getInt("total_sets"),
                                    getCompletedSetsForWorkout(completedWorkoutId)));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading completed workouts in range: " + e.getMessage());
        }
        return workouts;
    }

    /**
     * Returns the weekly workout tracking.
     * @param userId The user id.
     * @return weekly workout tracking
     */
    public boolean[] getWeeklyWorkoutTracking(int userId) {
        boolean[] streakDays = new boolean[7];
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<CompletedWorkoutData> workouts = getCompletedWorkoutsByUser(userId, date);
            streakDays[i] = workouts.size() > 0;
        }
        return streakDays;
    }

    /**
     * Insert completed sets.
     * @param completedWorkoutId The completed workout id.
     * @param completedSets The completed sets.
     */
    private void insertCompletedSets(int completedWorkoutId, List<CompletedSetData> completedSets)
            throws SQLException {
        String sql = """
                    INSERT INTO completed_workout_sets
                        (completed_workout_id, exercise_id, exercise_name, set_number,
                         target_reps, completed_reps, rest_seconds)
                    VALUES (?,?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CompletedSetData set : completedSets) {
                ps.setInt(1, completedWorkoutId);
                ps.setInt(2, set.exerciseId);
                ps.setString(3, set.exerciseName);
                ps.setInt(4, set.setNumber);
                ps.setInt(5, set.targetReps);
                ps.setInt(6, set.completedReps);
                ps.setInt(7, set.restSeconds);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Returns the completed sets for workout.
     * @param completedWorkoutId The completed workout id.
     * @return completed sets for workout
     */
    private List<CompletedSetData> getCompletedSetsForWorkout(int completedWorkoutId)
            throws SQLException {
        List<CompletedSetData> sets = new ArrayList<>();
        String sql = """
                    SELECT exercise_id, exercise_name, set_number, target_reps, completed_reps, rest_seconds
                    FROM completed_workout_sets
                    WHERE completed_workout_id=?
                    ORDER BY id ASC
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, completedWorkoutId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sets.add(
                            new CompletedSetData(
                                    rs.getInt("exercise_id"),
                                    rs.getString("exercise_name"),
                                    rs.getInt("set_number"),
                                    rs.getInt("target_reps"),
                                    rs.getInt("completed_reps"),
                                    rs.getInt("rest_seconds")));
                }
            }
        }
        return sets;
    }

    /**
     * Provides routine data functionality for LockedIn.
     * @author LockedIn Team
     * @version 1.0
     */
    public static class RoutineData {
        public final int id;
        public final String name;
        public final String notes;
        public final List<WorkoutExerciseEntry> exercises;

    /**
     * Constructs a RoutineData using default application dependencies.
     * @param id id
     * @param name name
     * @param notes notes
     * @param exercises exercises
     */
        public RoutineData(
                int id, String name, String notes, List<WorkoutExerciseEntry> exercises) {
            this.id = id;
            this.name = name;
            this.notes = notes != null ? notes : "";
            this.exercises = exercises;
        }
    }

    /**
     * Provides completed set data functionality for LockedIn.
     * @author LockedIn Team
     * @version 1.0
     */
    public static class CompletedSetData {
        public final int exerciseId;
        public final String exerciseName;
        public final int setNumber;
        public final int targetReps;
        public final int completedReps;
        public final int restSeconds;

    /**
     * Constructs a CompletedSetData using default application dependencies.
     * @param exerciseId The exercise id.
     * @param exerciseName The exercise name.
     * @param setNumber The set number.
     * @param targetReps The target reps.
     * @param completedReps The completed reps.
     * @param restSeconds The rest seconds.
     */
        public CompletedSetData(
                int exerciseId,
                String exerciseName,
                int setNumber,
                int targetReps,
                int completedReps,
                int restSeconds) {
            this.exerciseId = exerciseId;
            this.exerciseName = exerciseName;
            this.setNumber = setNumber;
            this.targetReps = targetReps;
            this.completedReps = completedReps;
            this.restSeconds = restSeconds;
        }
    }

    /**
     * Provides completed workout data functionality for LockedIn.
     * @author LockedIn Team
     * @version 1.0
     */
    public static class CompletedWorkoutData {
        public final int id;
        public final int routineId;
        public final String routineName;
        public final String completedAt;
        public final int totalExercises;
        public final int totalSets;
        public final List<CompletedSetData> sets;

    /**
     * Constructs a CompletedWorkoutData using default application dependencies.
     * @param id id
     * @param routineId The routine id.
     * @param routineName The routine name.
     * @param completedAt The completed at.
     * @param totalExercises The total exercises.
     * @param totalSets The total sets.
     * @param sets sets
     */
        public CompletedWorkoutData(
                int id,
                int routineId,
                String routineName,
                String completedAt,
                int totalExercises,
                int totalSets,
                List<CompletedSetData> sets) {
            this.id = id;
            this.routineId = routineId;
            this.routineName = routineName;
            this.completedAt = completedAt;
            this.totalExercises = totalExercises;
            this.totalSets = totalSets;
            this.sets = sets;
        }
    }
}

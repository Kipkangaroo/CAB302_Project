package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.WorkoutExerciseEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a full workout routine, including its metadata and the list of exercises that belong
 * to it.
 */
public class WorkoutRoutineDAO {

    public static class RoutineData {
        public final int id;
        public final String name;
        public final String notes;
        public final List<WorkoutExerciseEntry> exercises;

        public RoutineData(
                int id, String name, String notes, List<WorkoutExerciseEntry> exercises) {
            this.id = id;
            this.name = name;
            this.notes = notes != null ? notes : "";
            this.exercises = exercises;
        }
    }

    /**
     * Represents a single completed set during a workout session. Used for saving workout history
     * and generating summaries.
     */
    public static class CompletedSetData {
        public final int exerciseId;
        public final String exerciseName;
        public final int setNumber;
        public final int targetReps;
        public final int completedReps;
        public final int restSeconds;

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

    public static class CompletedWorkoutData {
        public final int id;
        public final int routineId;
        public final String routineName;
        public final String completedAt;
        public final int totalExercises;
        public final int totalSets;
        public final List<CompletedSetData> sets;

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

    private final Connection connection;

    public WorkoutRoutineDAO() {
        try {
            Path dbPath = Paths.get("LockedIn", "data", "users.db").toAbsolutePath();
            Files.createDirectories(dbPath.getParent());
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
            migrate();
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to connect to workout database", e);
        }
    }

    public WorkoutRoutineDAO(Connection connection) {
        this.connection = connection;
        createTables();
        migrate();
    }

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

    /** Adds columns that may be missing in databases created by an older version. */
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

    // ── Save ────────────────────────────────────────────────────────────────

    public int saveRoutine(
            int userId, String name, String notes, List<WorkoutExerciseEntry> entries) {
        String sql =
                "INSERT INTO workout_routines (user_id, name, notes, created_at) VALUES (?,?,?,?)";
        try (PreparedStatement ps =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    private void insertExercises(int routineId, List<WorkoutExerciseEntry> entries)
            throws SQLException {
        String sql =
                """
                    INSERT INTO routine_exercises
                        (routine_id, exercise_id, exercise_name, sets, reps, rest_seconds)
                    VALUES (?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (WorkoutExerciseEntry e : entries) {
                ps.setInt(1, routineId);
                ps.setInt(2, e.getExerciseId());
                ps.setString(3, e.getExerciseName());
                ps.setInt(4, e.getSets());
                ps.setInt(5, e.getReps());
                ps.setInt(6, e.getRestSeconds());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ── Read ────────────────────────────────────────────────────────────────

    public List<RoutineData> getRoutinesByUser(int userId) {
        List<RoutineData> routines = new ArrayList<>();
        String sql =
                "SELECT id, name, notes FROM workout_routines WHERE user_id=? ORDER BY created_at"
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

    private List<WorkoutExerciseEntry> getExercisesForRoutine(int routineId) throws SQLException {
        List<WorkoutExerciseEntry> entries = new ArrayList<>();
        String sql =
                "SELECT id, exercise_id, exercise_name, sets, reps, rest_seconds "
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

    // ── Update ──────────────────────────────────────────────────────────────

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

    public void addExerciseToRoutine(
            int routineId,
            int exerciseId,
            String exerciseName,
            int sets,
            int reps,
            int restSeconds) {
        String sql =
                """
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

    public void removeExerciseFromRoutine(int entryId) {
        String sql = "DELETE FROM routine_exercises WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing exercise: " + e.getMessage());
        }
    }

    // ── Delete ──────────────────────────────────────────────────────────────

    public void deleteRoutine(int routineId) {
        try (PreparedStatement ps1 =
                        connection.prepareStatement(
                                "DELETE FROM routine_exercises WHERE routine_id=?");
                PreparedStatement ps2 =
                        connection.prepareStatement("DELETE FROM workout_routines WHERE id=?")) {
            ps1.setInt(1, routineId);
            ps1.executeUpdate();
            ps2.setInt(1, routineId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting routine: " + e.getMessage());
        }
    }

    public int saveCompletedWorkout(
            int userId, RoutineData routine, List<CompletedSetData> completedSets) {
        String sql =
                """
                    INSERT INTO completed_workouts
                        (user_id, routine_id, routine_name, completed_at, total_exercises, total_sets)
                    VALUES (?,?,?,?,?,?)
                """;
        try (PreparedStatement ps =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, routine.id);
            ps.setString(3, routine.name);
            ps.setString(4, LocalDateTime.now().toString());
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

    /**
     * Completed workouts for a user on a specific calendar day ({@code completed_at} is matched by
     * SQLite {@code date(completed_at)}), newest first within that day.
     */
    public List<CompletedWorkoutData> getCompletedWorkoutsByUser(int userId, LocalDate onDate) {
        List<CompletedWorkoutData> workouts = new ArrayList<>();
        String sql =
                onDate == null
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

    public boolean[] getWeeklyWorkoutTracking(int userID) {
        boolean[] streakDays = new boolean[7];
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<CompletedWorkoutData> workouts = getCompletedWorkoutsByUser(userID, date);
            streakDays[i] = workouts.size() > 0;
        }
        return streakDays;
    }

    private void insertCompletedSets(int completedWorkoutId, List<CompletedSetData> completedSets)
            throws SQLException {
        String sql =
                """
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

    private List<CompletedSetData> getCompletedSetsForWorkout(int completedWorkoutId)
            throws SQLException {
        List<CompletedSetData> sets = new ArrayList<>();
        String sql =
                """
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
}

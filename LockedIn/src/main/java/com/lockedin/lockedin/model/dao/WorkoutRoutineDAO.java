package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.WorkoutExerciseEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkoutRoutineDAO {

    public static class RoutineData {
        public final int id;
        public final String name;
        public final String notes;
        public final List<WorkoutExerciseEntry> exercises;

        public RoutineData(int id, String name, String notes, List<WorkoutExerciseEntry> exercises) {
            this.id = id;
            this.name = name;
            this.notes = notes != null ? notes : "";
            this.exercises = exercises;
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

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS workout_routines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    notes TEXT DEFAULT '',
                    created_at TEXT NOT NULL
                )
            """);
            stmt.execute("""
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
        } catch (SQLException e) {
            System.err.println("Error creating workout tables: " + e.getMessage());
        }
    }

    /** Adds columns that may be missing in databases created by an older version. */
    private void migrate() {
        try (Statement stmt = connection.createStatement()) {
            try { stmt.execute("ALTER TABLE workout_routines ADD COLUMN notes TEXT DEFAULT ''"); }
            catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE routine_exercises ADD COLUMN rest_seconds INTEGER NOT NULL DEFAULT 60"); }
            catch (SQLException ignored) {}
        } catch (SQLException e) {
            System.err.println("Migration error: " + e.getMessage());
        }
    }

    // ── Save ────────────────────────────────────────────────────────────────

    public int saveRoutine(int userId, String name, String notes,
                           List<WorkoutExerciseEntry> entries) {
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

    private void insertExercises(int routineId, List<WorkoutExerciseEntry> entries)
            throws SQLException {
        String sql = """
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
        String sql = "SELECT id, name, notes FROM workout_routines WHERE user_id=? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                routines.add(new RoutineData(id, rs.getString("name"), rs.getString("notes"),
                        getExercisesForRoutine(id)));
            }
        } catch (SQLException e) {
            System.err.println("Error loading routines: " + e.getMessage());
        }
        return routines;
    }

    public RoutineData getRoutineById(int routineId) {
        String sql = "SELECT id, name, notes FROM workout_routines WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routineId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new RoutineData(rs.getInt("id"), rs.getString("name"), rs.getString("notes"),
                        getExercisesForRoutine(routineId));
            }
        } catch (SQLException e) {
            System.err.println("Error loading routine: " + e.getMessage());
        }
        return null;
    }

    private List<WorkoutExerciseEntry> getExercisesForRoutine(int routineId) throws SQLException {
        List<WorkoutExerciseEntry> entries = new ArrayList<>();
        String sql = "SELECT id, exercise_id, exercise_name, sets, reps, rest_seconds " +
                     "FROM routine_exercises WHERE routine_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routineId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entries.add(new WorkoutExerciseEntry(
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

    public void addExerciseToRoutine(int routineId, int exerciseId, String exerciseName,
                                     int sets, int reps, int restSeconds) {
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
        try (PreparedStatement ps1 = connection.prepareStatement(
                "DELETE FROM routine_exercises WHERE routine_id=?");
             PreparedStatement ps2 = connection.prepareStatement(
                "DELETE FROM workout_routines WHERE id=?")) {
            ps1.setInt(1, routineId);
            ps1.executeUpdate();
            ps2.setInt(1, routineId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting routine: " + e.getMessage());
        }
    }
}

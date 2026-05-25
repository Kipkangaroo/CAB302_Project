package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.user.Measurement;
import com.lockedin.lockedin.model.session.CurrentUser;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Data access object for user body measurements (weight, body fat, and similar types).
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class MeasurementDAO {

    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

    /**
     * Opens the default users database and ensures the measurements table exists.
     */
    public MeasurementDAO() {
        this.connection = SqliteConnection.getInstance(USERS_DB_FILE);
        createMeasurementsTable();
    }

    /**
     * Uses the supplied connection (for example an in-memory database in tests).
     *
     * @param connection active SQLite connection
     */
    public MeasurementDAO(Connection connection) {
        this.connection = connection;
        createMeasurementsTable();
    }

    /** Creates the measurements table when missing. */
    private void createMeasurementsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS measurements (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                value REAL NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create measurements table", e);
        }
    }

    /**
     * Inserts a measurement row for the given entity.
     *
     * @param m measurement to persist (user id, value, type, date)
     */
    public void addMeasurement(Measurement m) {
        String sql = "INSERT INTO measurements (user_id, value, type, date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, m.getUserId());
            stmt.setDouble(2, m.getValue());
            stmt.setString(3, m.getType());
            stmt.setString(4, m.getDate().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add measurement", e);
        }
    }

    /**
     * Returns all measurements of the given type for the currently logged-in user, oldest first.
     *
     * @param type measurement category (for example {@code Weight})
     * @return chronological list of matching measurements
     */
    public List<Measurement> getMeasurements(String type) {
        List<Measurement> list = new ArrayList<>();

        String sql = "SELECT * FROM measurements WHERE user_id = ? AND type = ? ORDER BY date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, CurrentUser.get().getId());
            stmt.setString(2, type);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Measurement(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("value"),
                        rs.getString("type"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch measurements", e);
        }

        return list;
    }
    /**
     * Deletes a measurement by primary key.
     *
     * @param id database id of the measurement row
     * @return {@code true} if exactly one row was removed
     */
    public boolean deleteMeasurement(int id) {
        String sql = "DELETE FROM measurements WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete measurement", e);
        }
    }

}

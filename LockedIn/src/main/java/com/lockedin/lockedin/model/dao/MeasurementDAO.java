package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.user.Measurement;
import com.lockedin.lockedin.model.session.CurrentUser;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Data Access Object (DAO) responsible for managing user body measurements.
 *
 * This class handles:
 * - Storing new measurements (e.g., weight, waist, body fat) for the logged‑in user
 * - Retrieving measurements filtered by type (Weight, Waist, Body Fat %)
 * - Ordering results by date to support trend analysis
 * - Deleting individual measurement entries when needed
 *
 * It acts as the data layer for the Measurements feature, enabling users
 * to track physical changes over time.
 */
public class MeasurementDAO {

    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

    public MeasurementDAO() {
        this.connection = SqliteConnection.getInstance(USERS_DB_FILE);
        createMeasurementsTable();
    }

    public MeasurementDAO(Connection connection) {
        this.connection = connection;
        createMeasurementsTable();
    }

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

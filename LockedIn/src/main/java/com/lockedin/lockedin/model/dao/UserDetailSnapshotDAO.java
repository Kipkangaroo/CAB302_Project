package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.UserDetailSnapshot;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UserDetailSnapshotDAO {
    private static final String USER_DETAIL_SNAPSHOTS_DB_FILE = "user_detail_snapshots.db";
    private final Connection connection;

    public UserDetailSnapshotDAO() {
        this.connection = SqliteConnection.getInstance(USER_DETAIL_SNAPSHOTS_DB_FILE);
        createUserDetailSnapshotsTable();
    }

    private void createUserDetailSnapshotsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_detail_snapshots ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "fitness_goal TEXT NOT NULL, "
                + "weight REAL NOT NULL, "
                + "effective_from TEXT NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create users table", e);
        }
    }

    public boolean addUserDetailSnapshot(UserDetailSnapshot userDetailSnapshot) {
        // check if a snapshot already exists for the given date and update it if it does
        Optional<UserDetailSnapshot> existingSnapshot = getUserDetailSnapshotByDate(userDetailSnapshot.getUserId(), userDetailSnapshot.getEffectiveFrom());
        if (existingSnapshot.isPresent()) {
            UserDetailSnapshot existing = existingSnapshot.get();
            String updateSql = "UPDATE user_detail_snapshots SET fitness_goal = ?, weight = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                FitnessGoal fitnessGoal = userDetailSnapshot.getFitnessGoal();
                preparedStatement.setString(
                        1, fitnessGoal == null ? null : fitnessGoal.getDisplayName());
                preparedStatement.setDouble(2, userDetailSnapshot.getWeight());
                preparedStatement.setInt(3, existing.getId());
                return preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update user detail snapshot", e);
            }
        }

        // if no snapshot exists for the given date, add a new one
        String sql = "INSERT INTO user_detail_snapshots(user_id, fitness_goal, weight, effective_from) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userDetailSnapshot.getUserId());
            FitnessGoal fitnessGoal = userDetailSnapshot.getFitnessGoal();
            preparedStatement.setString(
                    2, fitnessGoal == null ? null : fitnessGoal.getDisplayName());
            preparedStatement.setDouble(3, userDetailSnapshot.getWeight());
            preparedStatement.setString(4, userDetailSnapshot.getEffectiveFrom().toString());
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                return false;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userDetailSnapshot.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add user detail snapshot", e);
        }
    }

    public Optional<UserDetailSnapshot> getUserDetailSnapshotByDate(int userId, LocalDate date) {
        String sql = "SELECT * FROM user_detail_snapshots WHERE user_id = ? AND effective_from = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, date.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUserDetailSnapshot(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user detail snapshot by date", e);
        }
    }

    public Optional<UserDetailSnapshot> getLatestUserDetailSnapshot(int userId) {
        String sql = "SELECT * FROM user_detail_snapshots WHERE user_id = ? ORDER BY effective_from DESC, id DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUserDetailSnapshot(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch latest user detail snapshot", e);
        }
    }

    public List<UserDetailSnapshot> getUserDetailSnapshots(int userId) {
        String sql = "SELECT * FROM user_detail_snapshots WHERE user_id = ? ORDER BY effective_from DESC, id DESC";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<UserDetailSnapshot> snapshots = new ArrayList<>();
                while (resultSet.next()) {
                    snapshots.add(mapUserDetailSnapshot(resultSet));
                }
                return snapshots;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user detail snapshots", e);
        }
    }

    public Optional<UserDetailSnapshot> getActiveUserDetailSnapshot(int userId, LocalDate date) {
        return getUserDetailSnapshots(userId).stream()
                .filter(snapshot -> !snapshot.getEffectiveFrom().isAfter(date))
                .max(Comparator.comparing(UserDetailSnapshot::getEffectiveFrom)
                        .thenComparing(UserDetailSnapshot::getId)); 
    }

    private UserDetailSnapshot mapUserDetailSnapshot(ResultSet resultSet) throws SQLException {
        return new UserDetailSnapshot(
            resultSet.getInt("id"),
            resultSet.getInt("user_id"),
            FitnessGoal.fromDisplayName(resultSet.getString("fitness_goal")),
            resultSet.getDouble("weight"),
            LocalDate.parse(resultSet.getString("effective_from")));
    }
}

package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Data access object for user records.
 * @author LockedIn Team
 * @version 1.0
 */
public class UserDAO {
    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

        /**
     * Constructs a UserDAO using default application dependencies.
     */
    public UserDAO() {
        this.connection = SqliteConnection.getInstance(USERS_DB_FILE);
        createUsersTable();
    }

        /**
     * Constructs a UserDAO using default application dependencies.
     * @param connection connection
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
        createUsersTable();
    }

        /**
     * Create users table.
     */
    private void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT NOT NULL, "
                + "last_name TEXT NOT NULL, "
                + "email TEXT NOT NULL UNIQUE, "
                + "date_of_birth TEXT NOT NULL, "
                + "height REAL NOT NULL, "
                + "weight REAL NOT NULL, "
                + "sex TEXT, "
                + "activity_level TEXT, "
                + "fitness_goal TEXT, "
                + "password_hash TEXT NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create users table", e);
        }
    }

        /**
     * Create user.
     * @param user user
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users(first_name, last_name, email, date_of_birth, height, weight, sex, activity_level, fitness_goal, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getDateOfBirth().toString());
            preparedStatement.setDouble(5, user.getHeight());
            preparedStatement.setDouble(6, user.getWeight());
            preparedStatement.setString(7, user.getSex());
            ActivityLevel activityLevel = user.getActivityLevel();
            preparedStatement.setString(
                    8, activityLevel == null ? null : activityLevel.getDisplayName());
            FitnessGoal fitnessGoal = user.getFitnessGoal();
            preparedStatement.setString(
                    9, fitnessGoal == null ? null : fitnessGoal.getDisplayName());
            preparedStatement.setString(10, user.getPasswordHash());
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                return false;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

            /**
     * Returns the user by id.
     * @param id id
     * @return user by id
     */
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by id", e);
        }
    }

        /**
     * Update first name.
     * @param id id
     * @param firstName The first name.
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean updateFirstName(int id, String firstName) {
        String sql = "UPDATE users SET first_name = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update first name", e);
        }
    }

        /**
     * Update fitness goal.
     * @param id id
     * @param fitnessGoal The fitness goal.
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean updateFitnessGoal(int id, FitnessGoal fitnessGoal) {
        String sql = "UPDATE users SET fitness_goal = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, fitnessGoal.getDisplayName());
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update fitness goal", e);
        }
    }

        /**
     * Update weight.
     * @param id id
     * @param weight weight
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean updateWeight(int id, double weight) {
        String sql = "UPDATE users SET weight = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, weight);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update weight", e);
        }
    }

            /**
     * Returns the user by email.
     * @param email email
     * @return user by email
     */
    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by email", e);
        }
    }

        /**
     * Authenticate.
     * @param email email
     * @param plainPassword The plain password.
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean authenticate(String email, String plainPassword) {
        Optional<User> user = getUserByEmail(email);
        if (user.isEmpty()) {
            return false;
        }
        String providedHash = User.getHash(plainPassword);
        return providedHash.equals(user.get().getPasswordHash());
    }

        /**
     * Delete user.
     * @param id id
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

        /**
     * Update password.
     * @param email email
     * @param plainPassword The plain password.
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean updatePassword(String email, String plainPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, User.getHash(plainPassword));
            preparedStatement.setString(2, email);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }

        /**
     * Map user.
     * @param resultSet The result set.
     * @throws SQLException If the operation fails.
     */
    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setDateOfBirth(LocalDate.parse(resultSet.getString("date_of_birth")));
        user.setHeight(resultSet.getDouble("height"));
        user.setWeight(resultSet.getDouble("weight"));
        user.setSex(resultSet.getString("sex"));
        user.setActivityLevel(ActivityLevel.fromDisplayName(resultSet.getString("activity_level")));
        user.setFitnessGoal(FitnessGoal.fromDisplayName(resultSet.getString("fitness_goal")));
        user.setPasswordHash(resultSet.getString("password_hash"));
        return user;
    }
}

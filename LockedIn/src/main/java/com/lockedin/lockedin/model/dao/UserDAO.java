package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Data Access Object for user accounts. Handles creation of the users table and
 * provides a
 * connection for saving and retrieving user profile and authentication data.
 */
public class UserDAO {
    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

    public UserDAO() {
        this.connection = SqliteConnection.getInstance(USERS_DB_FILE);
        createUsersTable();
    }

    public UserDAO(Connection connection) {
        this.connection = connection;
        createUsersTable();
    }

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

    public boolean authenticate(String email, String plainPassword) {
        Optional<User> user = getUserByEmail(email);
        if (user.isEmpty()) {
            return false;
        }
        String providedHash = User.getHash(plainPassword);
        return providedHash.equals(user.get().getPasswordHash());
    }

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

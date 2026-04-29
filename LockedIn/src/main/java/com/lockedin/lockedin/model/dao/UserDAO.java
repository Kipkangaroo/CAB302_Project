package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.User;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class UserDAO {
    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

    public UserDAO() {
        this.connection = SqliteConnection.getInstance(USERS_DB_FILE);
        createUsersTable();
    }

    /** For testing: accepts an existing (e.g. in-memory) connection. */
    public UserDAO(Connection connection) {
        this.connection = connection;
        createUsersTable();
    }

    private void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "date_of_birth TEXT NOT NULL, " +
                "height REAL NOT NULL, " +
                "weight REAL NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "fitness_goal TEXT, " +
                "password_hash TEXT NOT NULL" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to create users table", exception);
        }
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users(first_name, last_name, date_of_birth, height, weight, email, fitness_goal, password_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getDateOfBirth().toString());
            preparedStatement.setDouble(4, user.getHeight());
            preparedStatement.setDouble(5, user.getWeight());
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getFitnessGoal());
            preparedStatement.setString(8, user.getPasswordHash());
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
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to create user", exception);
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
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to fetch user by id", exception);
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
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to fetch user by email", exception);
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
        user.setDateOfBirth(parseDate(resultSet.getString("date_of_birth")));
        user.setHeight(resultSet.getDouble("height"));
        user.setWeight(resultSet.getDouble("weight"));
        user.setEmail(resultSet.getString("email"));
        user.setFitnessGoal(resultSet.getString("fitness_goal"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        return user;
    }

    private LocalDate parseDate(String dateText) {
        return LocalDate.parse(dateText);
    }
}

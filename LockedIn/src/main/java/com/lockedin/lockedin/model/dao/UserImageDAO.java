package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Data access object for user profile images stored in users.db.
 */
public class UserImageDAO {
    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

    public UserImageDAO() {
        this(SqliteConnection.getInstance(USERS_DB_FILE));
    }

    public UserImageDAO(Connection connection) {
        this.connection = connection;
        createUserImagesTable();
    }

    private void createUserImagesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_images ("
                + "id INTEGER PRIMARY KEY, "
                + "image BLOB NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user_images table", e);
        }
    }

    public void saveOrReplaceImage(int userId, byte[] image) {
        String sql = "INSERT OR REPLACE INTO user_images (id, image) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setBytes(2, image);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save image for user " + userId, e);
        }
    }

    public Optional<byte[]> getImageByUserId(int userId) {
        String sql = "SELECT image FROM user_images WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.ofNullable(resultSet.getBytes("image"));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch image for user " + userId, e);
        }
    }

    public void deleteImage(int userId) {
        String sql = "DELETE FROM user_images WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete image for user " + userId, e);
        }
    }
}

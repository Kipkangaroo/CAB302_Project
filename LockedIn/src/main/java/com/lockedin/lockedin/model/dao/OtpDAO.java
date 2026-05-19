package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Random;

/**
 * Data access for one-time password codes linked to user accounts. Each user
 * has
 * at most one active OTP; saving again replaces the previous code.
 */
public class OtpDAO {
    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;
    private final UserDAO userDAO;

    public OtpDAO() {
        this(SqliteConnection.getInstance(USERS_DB_FILE));
    }

    public OtpDAO(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO();
        createOtpTable();
    }

    private void createOtpTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_otps ("
                + "email TEXT PRIMARY KEY, "
                + "otp_code INTEGER NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user_otps table", e);
        }
    }

    public void saveOrReplaceOtp(String email, int otpCode) {
        String sql = "INSERT OR REPLACE INTO user_otps (email, otp_code) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, otpCode);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save OTP for user " + email, e);
        }
    }

    public Optional<Integer> getOtpCode(String email) {
        String sql = "SELECT otp_code FROM user_otps WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getInt("otp_code"));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch OTP for user " + email, e);
        }
    }

    public boolean verifyOtp(String email, int otpCode) {
        Optional<Integer> storedOtpCode = getOtpCode(email);
        return storedOtpCode.isPresent() && storedOtpCode.get() == otpCode;
    }

    public void deleteOtp(String email) {
        String sql = "DELETE FROM user_otps WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete OTP for user " + email, e);
        }
    }
}

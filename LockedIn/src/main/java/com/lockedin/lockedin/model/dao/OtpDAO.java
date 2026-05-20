package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Data access object for otp records.
 * @author LockedIn Team
 * @version 1.0
 */
public class OtpDAO {
    private static final String USERS_DB_FILE = "users.db";
    private final Connection connection;

    /**
     * Creates a new OtpDAO.
     */
    public OtpDAO() {
        this(SqliteConnection.getInstance(USERS_DB_FILE));
    }

    /**
     * Creates a new OtpDAO.
     * @param connection The connection.
     */
    public OtpDAO(Connection connection) {
        this.connection = connection;
        createOtpTable();
    }

    /**
     * Performs create otp table.
     */
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

    /**
     * Performs save or replace otp.
     * @param email The email.
     * @param otpCode The otp code.
     */
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

    /**
     * Returns the otp code.
     * @param email The email.
     * @return The otp code.
     */
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

    /**
     * Performs verify otp.
     * @param email The email.
     * @param otpCode The otp code.
     * @return true if the operation succeeds; otherwise false.
     */
    public boolean verifyOtp(String email, int otpCode) {
        Optional<Integer> storedOtpCode = getOtpCode(email);
        return storedOtpCode.isPresent() && storedOtpCode.get() == otpCode;
    }

    /**
     * Performs delete otp.
     * @param email The email.
     */
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

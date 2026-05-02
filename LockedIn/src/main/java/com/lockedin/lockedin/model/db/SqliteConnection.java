package com.lockedin.lockedin.model.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton-style SQLite connection manager. Ensures each database file is opened once and reused
 * across DAOs.
 */
public class SqliteConnection {
    private static Connection instance = null;
    private static final Path DB_DIRECTORY = Paths.get("LockedIn", "data").toAbsolutePath();
    private static Path activeDbPath = null;

    private SqliteConnection(String dbFileName) {
        Path dbPath = DB_DIRECTORY.resolve(dbFileName).toAbsolutePath();
        String url = "jdbc:sqlite:" + dbPath;
        try {
            Files.createDirectories(dbPath.getParent());
            instance = DriverManager.getConnection(url);
            activeDbPath = dbPath;
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(
                    "Failed to connect to SQLite database at " + dbPath, exception);
        }
    }

    public static Connection getInstance(String dbFileName) {
        Path requestedDbPath = DB_DIRECTORY.resolve(dbFileName).toAbsolutePath();

        if (instance == null) {
            new SqliteConnection(dbFileName);
        } else if (!requestedDbPath.equals(activeDbPath)) {
            try {
                instance.close();
            } catch (SQLException ignored) {
            }
            new SqliteConnection(dbFileName);
        }
        return instance;
    }
}

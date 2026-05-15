package com.lockedin.lockedin.model.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQLite connection manager. Opens each database file at most once and reuses the connection
 * across DAOs. Connections for different files are independent; accessing one file never closes
 * another.
 */
public final class SqliteConnection {
    private static final Path DB_DIRECTORY = Paths.get("LockedIn", "data").toAbsolutePath();
    private static final Map<Path, Connection> CONNECTIONS = new ConcurrentHashMap<>();

    private SqliteConnection() {}

    public static Connection getInstance(String dbFileName) {
        Path dbPath = DB_DIRECTORY.resolve(dbFileName).toAbsolutePath();
        return CONNECTIONS.computeIfAbsent(dbPath, SqliteConnection::openConnection);
    }

    private static Connection openConnection(Path dbPath) {
        String url = "jdbc:sqlite:" + dbPath;
        try {
            Files.createDirectories(dbPath.getParent());
            return DriverManager.getConnection(url);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(
                    "Failed to connect to SQLite database at " + dbPath, exception);
        }
    }
}

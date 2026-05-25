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
 * Provides sqlite connection functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public final class SqliteConnection {
    private static final Path DB_DIRECTORY = resolveModuleDataDirectory();
    private static final Map<Path, Connection> CONNECTIONS = new ConcurrentHashMap<>();

        /**
     * Constructs a SqliteConnection using default application dependencies.
     */
    private SqliteConnection() {
    }

    /**
     * Resolves {@code data/} beside the module {@code pom.xml} (LockedIn project root).
     */
    private static Path resolveModuleDataDirectory() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        Path nestedModule = cwd.resolve("LockedIn");
        if (Files.isRegularFile(nestedModule.resolve("pom.xml"))) {
            return nestedModule.resolve("data").toAbsolutePath();
        }
        if (Files.isRegularFile(cwd.resolve("pom.xml"))) {
            return cwd.resolve("data").toAbsolutePath();
        }
        throw new IllegalStateException(
                "Could not find pom.xml to locate the data directory. Working directory: " + cwd);
    }

    /**
     * Returns the resolved application data directory containing SQLite files.
     *
     * @return path to the data folder
     */
    public static Path getDataDirectory() {
        return DB_DIRECTORY;
    }

            /**
     * Returns the instance.
     * @param dbFileName The db file name.
     * @return instance
     */
    public static Connection getInstance(String dbFileName) {
        Path dbPath = DB_DIRECTORY.resolve(dbFileName).toAbsolutePath();
        return CONNECTIONS.computeIfAbsent(dbPath, SqliteConnection::openConnection);
    }

        /**
     * Open connection.
     * @param dbPath The db path.
     */
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

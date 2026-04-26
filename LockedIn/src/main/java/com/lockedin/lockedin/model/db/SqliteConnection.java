package com.lockedin.lockedin.model.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {
    private static Connection instance = null;
    private static final Path DB_PATH = Paths.get("LockedIn", "data", "exercises.db").toAbsolutePath();

    private SqliteConnection() {
        String url = "jdbc:sqlite:" + DB_PATH;
        try {
            Files.createDirectories(DB_PATH.getParent());
            instance = DriverManager.getConnection(url);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException("Failed to connect to SQLite database at " + DB_PATH, exception);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteConnection();
        }
        return instance;
    }
}

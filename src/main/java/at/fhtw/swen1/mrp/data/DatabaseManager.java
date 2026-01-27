package at.fhtw.swen1.mrp.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class to manage the database connection and initialization.
 */
public final class DatabaseManager {
    /** Singleton instance of the DatabaseManager. */
    private static DatabaseManager instance;
    /** The active database connection. */
    private Connection connection;

    /**
     * Private constructor to prevent instantiation.
     * Initializes the database connection.
     */
    private DatabaseManager() {
        try {
            String dbUrl = "jdbc:postgresql://localhost:5432/mrp_db?user=postgres&password=letmein";
            this.connection = DriverManager.getConnection(dbUrl);
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    /**
     * Returns the singleton instance of the DatabaseManager.
     *
     * @return the singleton instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Returns the current database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        // Note: a simple check if connection is closed/invalid
        // could be added here
        return connection;
    }

    /**
     * Resets the database by dropping all tables and re-initializing them.
     */
    public void resetDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS favorites, rating_likes, ratings, media_genres, media, users CASCADE");
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset database", e);
        }
    }

    /**
     * Initializes the database schema using the init script.
     */
    private void initializeDatabase() {
        try {
            String initScript = readInitScript();
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(initScript);
                System.out.println("Database initialized successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }
    // CHECKSTYLE:ON: RegexpSinglelineJava
    // CHECKSTYLE:ON: IllegalCatch

    /**
     * Reads the database initialization script from resources.
     *
     * @return the content of the init script
     * @throws IOException if the script cannot be read
     */
    private String readInitScript() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db/init.sql")) {
            if (is == null) {
                throw new IOException("db/init.sql not found");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        }
    }
}

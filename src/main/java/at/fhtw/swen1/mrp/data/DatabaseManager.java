package at.fhtw.swen1.mrp.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            String dbUrl = "jdbc:postgresql://localhost:5432/mrp_db?user=postgres&password=letmein";
            this.connection = DriverManager.getConnection(dbUrl);
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        // TODO: a simple check if connection is closed/invalid
        return connection;
    }

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

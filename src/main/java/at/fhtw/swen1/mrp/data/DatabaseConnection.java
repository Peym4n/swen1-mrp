package at.fhtw.swen1.mrp.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/mrp_db?user=postgres&password=letmein";
        return DriverManager.getConnection(dbUrl);
    }

    public static void executeInitScript() {
        try (Connection conn = getConnection()) {
            String initScript = readInitScript();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(initScript);
                System.out.println("Database initialized successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    private static String readInitScript() throws IOException {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db/init.sql")) {
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

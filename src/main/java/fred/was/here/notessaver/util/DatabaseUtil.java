package fred.was.here.notessaver.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASS;

    static {
        Properties props = new Properties();
        // looks for .env in the working directory (project root when run from IntelliJ)
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Could not load .env file", e);
        }

        DB_URL  = props.getProperty("DB_URL");
        DB_USER = props.getProperty("DB_USER");
        DB_PASS = props.getProperty("DB_PASS");
    }

    private DatabaseUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("DB connection failed: " + e.getMessage());
            return false;
        }
    }
}
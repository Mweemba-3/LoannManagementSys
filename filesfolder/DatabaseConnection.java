import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public final class DatabaseConnection {
    // Configuration (ideally load from environment variables or config file)
    private static final String URL = "jdbc:mysql://localhost:3306/loan_management_system";
    private static final String USER = "root"; // Replace with a non-root user in production
    private static final String PASSWORD = ""; // Never hardcode passwords in production!

    // Thread-safe connection with ThreadLocal
    private static final ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(() -> {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                URL + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                USER, PASSWORD
            );
            System.out.println("Database connection established.");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    });

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        Connection conn = connectionThreadLocal.get();
        if (conn == null || conn.isClosed()) {
            connectionThreadLocal.remove(); // Clean up stale connection
            conn = connectionThreadLocal.get(); // Re-initialize
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            Connection conn = connectionThreadLocal.get();
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        } finally {
            connectionThreadLocal.remove(); // Ensure cleanup
        }
    }

    // Test the connection (optional)
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connection test successful: " + conn.isValid(2));
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
        }
    }
}
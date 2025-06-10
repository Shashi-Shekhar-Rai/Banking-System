

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root"; // <-- CHANGE THIS TO YOUR MYSQL USERNAME
    private static final String DB_PASSWORD = "Ujjwal@123"; // <-- CHANGE THIS TO YOUR MYSQL PASSWORD

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
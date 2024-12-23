package income_and_expense_tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_income_tracker"; // Change to your database URL
    private static final String DB_USER = "root"; // Change to your username
    private static final String DB_PASSWORD = "animeshse22uecm058"; // Change to your password

    public static Connection getConnection() throws ClassNotFoundException {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connection Established");
        }catch (SQLException e) {
            System.out.println("Connection Failed "+ e.getMessage());
        }

        return conn;
    }
}

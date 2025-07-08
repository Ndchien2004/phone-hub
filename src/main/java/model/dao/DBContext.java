package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {
    private static final String USER = "sa";
    private static final String PASS = "123";
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=SmartphoneStoreDB;encrypt=true;TrustServerCertificate=True";
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "JDBC Driver not found", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void main(String[] args) {
        try (Connection connection = DBContext.getConnection()) {
            if (connection != null) {
                System.out.println("Connected to the database!");
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "SQL Error", ex);
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}

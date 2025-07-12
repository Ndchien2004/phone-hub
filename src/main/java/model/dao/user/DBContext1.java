package model.dao.user;

import model.dao.DBContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext1 {

    private static final Logger logger = LogManager.getLogger(DBContext.class);
    protected Connection connection;

    public DBContext1() {
        try {
            String username = "sa";
            String password = "123";
            String url = "jdbc:sqlserver://localhost:1433;databaseName=SWD392_PhoneHubDB;encrypt=true;trustServerCertificate=true;";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, username, password);
            logger.info("✅ Database connection established.");
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error("❌ Failed to connect to database", ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

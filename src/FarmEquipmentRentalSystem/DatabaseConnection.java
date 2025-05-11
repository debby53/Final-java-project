package FarmEquipmentRentalSystem;

import java.sql.*;

public class DatabaseConnection {
    public static Connection connect() {
        try {
            // Load Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish connection (replace 'localhost' and 'XE' with your host and SID if necessary)
            String url = "jdbc:oracle:thin:@localhost:1522:orcl";
            String username = "system";  // Your Oracle username
            String password = "123";  // Your Oracle password

            // Return the connection
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

import java.sql.*;

public class Database {
    private Connection conn;
    private String dbURL = "jdbc:mysql://localhost:3306/dbsales";
    private String username = "root";
    private String password = "password";

    public Connection getConn(){
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection failed. Error: " + e.getMessage());
            return conn;
        }
    }

    public void closeConn() throws SQLException {
        conn.close();
    }

    public void commit() throws SQLException {
        conn.commit();
    }
}

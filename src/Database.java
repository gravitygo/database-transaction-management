import java.sql.*;

public class Database {
    private Connection conn;
    private String dbURL = "jdbc:mysql://localhost:3306/dbsales";
    private String username = "root";
    private String password = "password";

    private void connect(){
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database.");
        } catch (
                SQLException e) {
            System.out.println("Connection failed. Error: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error in closing the connection: " + ex.getMessage());
            }
        }
    }
    public ResultSet useQuery(String query, String[] values) throws SQLException {
        connect();
        conn.setAutoCommit(false);

        PreparedStatement pstmt = conn.prepareStatement(query);

        for (int i = 0; i < values.length; i++)
            pstmt.setString(i + 1, values[i]);

        ResultSet rs = pstmt.executeQuery();

        pstmt.close();
        conn.commit();
        conn.close();
        return rs;
    }
}

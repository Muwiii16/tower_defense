import java.sql.*;

public class DatabaseManager {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "tower_of_engkanto";
    private static final String URL = BASE_URL + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = null;

    private Connection connection;

    public DatabaseManager() {
        try {
            Connection baseConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
            Statement stmt = baseConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database ready!");
            baseConn.close();

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            createTables();
            System.out.println("Tables ready!");

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) UNIQUE NOT NULL," +
                        "password VARCHAR(50) NOT NULL" +
                        ")");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS leaderboard (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) NOT NULL," +
                        "score INT NOT NULL," +
                        "difficulty VARCHAR(10) NOT NULL," +
                        "stage_reached INT NOT NULL," +
                        "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS game_saves (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) UNIQUE NOT NULL," +
                        "unlocked_stage INT DEFAULT 1," +
                        "last_completed_stage INT DEFAULT 0," +
                        "difficulty VARCHAR(10) DEFAULT 'easy'," +
                        "total_points INT DEFAULT 0" +
                        ")");
    }

    public boolean validateLogin(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return false;

        }
    }

    public boolean registerUser(String username, String password) {
        try {
            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();

            String saveQuery = "INSERT INTO game_saves (username, unlocked_stages, last_completed_stage, difficulty, total_points) VALUES (?, 1, 0, 'easy',0)";
            PreparedStatement saveStmt = connection.prepareStatement(saveQuery);
            saveStmt.setString(1, username);
            saveStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

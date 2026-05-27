package database;

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

            String saveQuery = "INSERT INTO game_saves (username, unlocked_stage, last_completed_stage, difficulty, total_points) VALUES (?, 1, 0, 'easy',0)";
            PreparedStatement saveStmt = connection.prepareStatement(saveQuery);
            saveStmt.setString(1, username);
            saveStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public int getUnlockedStage(String username) {
        try {
            String query = "SELECT unlocked_stage FROM game_saves WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("unlocked_stage");
        } catch (SQLException e) {
            System.err.println("Error getting unlocked stage: " + e.getMessage());
        }
        return 1;
    }

    public java.util.List<String[]> getTopLeaderboard() {
        java.util.List<String[]> results = new java.util.ArrayList<>();
        try {
            String query = "SELECT username, SUM(score) as total_points, " +
                    "MAX(stage_reached) as best_stage, " +
                    "MAX(CASE WHEN difficulty='hard' THEN 3 " +
                    "WHEN difficulty='normal' THEN 2 ELSE 1 END) as diff_rank, " +
                    "MAX(difficulty) as best_difficulty, " +
                    "MAX(date) as last_played " +
                    "FROM leaderboard " +
                    "GROUP BY username " +
                    "ORDER BY total_points DESC " +
                    "LIMIT 10";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new String[] {
                        rs.getString("username"),
                        String.valueOf(rs.getInt("total_points")),
                        String.valueOf(rs.getInt("best_stage")),
                        rs.getString("best_difficulty"),
                        rs.getString("last_played")
                });
            }
        } catch (SQLException e) {
            System.err.println("Leaderboard error: " + e.getMessage());
        }
        return results;
    }

    public void saveGameProgress(String username, int stageCompleted, String difficulty, int score) {
        try {
            System.out.println("Saving progress: " + username + " stage:" + stageCompleted + " score:" + score);
            // Update unlocked stage if new stage completed
            String updateSave = "UPDATE game_saves SET " +
                    "last_completed_stage = GREATEST(last_completed_stage, ?), " +
                    "unlocked_stage = GREATEST(unlocked_stage, ?), " +
                    "difficulty = ?, " +
                    "total_points = total_points + ? " +
                    "WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(updateSave);
            stmt.setInt(1, stageCompleted);
            stmt.setInt(2, Math.min(stageCompleted + 1, 3));
            stmt.setString(3, difficulty);
            stmt.setInt(4, score);
            stmt.setString(5, username);
            stmt.executeUpdate();

            // Add to leaderboard
            String insertLdb = "INSERT INTO leaderboard " +
                    "(username, score, difficulty, stage_reached) VALUES (?, ?, ?, ?)";
            PreparedStatement ldbStmt = connection.prepareStatement(insertLdb);
            ldbStmt.setString(1, username);
            ldbStmt.setInt(2, score);
            ldbStmt.setString(3, difficulty);
            ldbStmt.setInt(4, stageCompleted);
            ldbStmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving progress: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

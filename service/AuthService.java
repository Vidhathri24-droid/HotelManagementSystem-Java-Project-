
package service;

import java.sql.*;

public class AuthService {
    public static boolean login(String username, String password, String role) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE username=? AND password=? AND role=?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean signup(String username, String password) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, 'customer')")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

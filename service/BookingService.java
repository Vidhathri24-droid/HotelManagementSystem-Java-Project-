package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    public static int getUserIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String> getHotelBranches() {
        List<String> branches = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM hotel_branches")) {
            while (rs.next()) {
                branches.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return branches;
    }

    public static List<String> getAvailableRooms(String branchName) {
        List<String> rooms = new ArrayList<>();
        String query = "SELECT r.id, r.room_type FROM rooms r " +
                       "JOIN hotel_branches hb ON r.branch_id = hb.id " +
                       "WHERE hb.name = ? AND r.is_available = TRUE";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, branchName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add("Room ID: " + rs.getInt("id") + " | Type: " + rs.getString("room_type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static int bookRoomWithGuestsAndReturnId(int userId, int roomId, String checkIn, String checkOut, double total, String guestNamesCSV) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "")) {
            conn.setAutoCommit(false);

            PreparedStatement bookStmt = conn.prepareStatement(
                "INSERT INTO bookings (user_id, room_id, checkin_date, checkout_date, total_cost) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            bookStmt.setInt(1, userId);
            bookStmt.setInt(2, roomId);
            bookStmt.setDate(3, Date.valueOf(checkIn));
            bookStmt.setDate(4, Date.valueOf(checkOut));
            bookStmt.setDouble(5, total);
            bookStmt.executeUpdate();

            ResultSet rs = bookStmt.getGeneratedKeys();
            int bookingId = -1;
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }

            if (bookingId != -1) {
                PreparedStatement guestStmt = conn.prepareStatement("INSERT INTO guests (booking_id, name) VALUES (?, ?)");
                for (String name : guestNamesCSV.split(",")) {
                    guestStmt.setInt(1, bookingId);
                    guestStmt.setString(2, name.trim());
                    guestStmt.addBatch();
                }
                guestStmt.executeBatch();

                PreparedStatement updateRoom = conn.prepareStatement("UPDATE rooms SET is_available = FALSE WHERE id = ?");
                updateRoom.setInt(1, roomId);
                updateRoom.executeUpdate();
            }

            conn.commit();
            return bookingId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean cancelBooking(int bookingId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "")) {
            PreparedStatement roomStmt = conn.prepareStatement("SELECT room_id FROM bookings WHERE id = ?");
            roomStmt.setInt(1, bookingId);
            ResultSet rs = roomStmt.executeQuery();
            if (rs.next()) {
                int roomId = rs.getInt("room_id");

                PreparedStatement delStmt = conn.prepareStatement("DELETE FROM bookings WHERE id = ?");
                delStmt.setInt(1, bookingId);
                delStmt.executeUpdate();

                PreparedStatement updStmt = conn.prepareStatement("UPDATE rooms SET is_available = TRUE WHERE id = ?");
                updStmt.setInt(1, roomId);
                updStmt.executeUpdate();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void refreshRoomAvailability() {
        String query = "UPDATE rooms r SET r.is_available = TRUE " +
                       "WHERE r.id NOT IN (SELECT room_id FROM bookings WHERE checkout_date >= CURDATE())";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllBookings() {
        List<String> bookings = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT b.id, u.username, r.id AS room_id, r.room_type, b.checkin_date, b.checkout_date, b.total_cost " +
                 "FROM bookings b JOIN users u ON b.user_id = u.id JOIN rooms r ON b.room_id = r.id")) {
            while (rs.next()) {
                bookings.add("Booking ID: " + rs.getInt("id") +
                             " | Customer: " + rs.getString("username") +
                             " | Room: " + rs.getInt("room_id") + " (" + rs.getString("room_type") + ")" +
                             " | Check-In: " + rs.getDate("checkin_date") +
                             " | Check-Out: " + rs.getDate("checkout_date") +
                             " | Cost: $" + rs.getDouble("total_cost"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static List<String> getBookingsByCustomerName(String name) {
        List<String> bookings = new ArrayList<>();
        String query = "SELECT b.id, u.username, r.id AS room_id, r.room_type, b.checkin_date, b.checkout_date, b.total_cost " +
                       "FROM bookings b JOIN users u ON b.user_id = u.id " +
                       "JOIN rooms r ON b.room_id = r.id " +
                       "WHERE u.username LIKE ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add("Booking ID: " + rs.getInt("id") +
                             " | Customer: " + rs.getString("username") +
                             " | Room: " + rs.getInt("room_id") + " (" + rs.getString("room_type") + ")" +
                             " | Check-In: " + rs.getDate("checkin_date") +
                             " | Check-Out: " + rs.getDate("checkout_date") +
                             " | Cost: $" + rs.getDouble("total_cost"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static List<String> getAllAvailableRooms() {
        List<String> rooms = new ArrayList<>();
        String sql = "SELECT r.id, r.room_type, r.price, hb.name AS branch FROM rooms r " +
                     "JOIN hotel_branches hb ON r.branch_id = hb.id WHERE r.is_available = TRUE";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add("Room ID: " + rs.getInt("id") +
                          " | Type: " + rs.getString("room_type") +
                          " | Price: $" + rs.getDouble("price") +
                          " | Branch: " + rs.getString("branch"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static boolean addRoom(int branchId, String roomType, double price) {
        String sql = "INSERT INTO rooms (branch_id, room_type, price, is_available) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setString(2, roomType);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

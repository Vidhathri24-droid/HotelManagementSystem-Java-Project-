
package service;

import java.sql.*;

public class BillingService {
    public static double calculateBill(int roomId, String checkIn, String checkOut, int numPeople) {
        double pricePerNight = 0;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db1", "root", "09082024");
             PreparedStatement stmt = conn.prepareStatement("SELECT price FROM rooms WHERE id = ?")) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pricePerNight = rs.getDouble("price");
            }

            long nights = (Date.valueOf(checkOut).getTime() - Date.valueOf(checkIn).getTime()) / (1000 * 60 * 60 * 24);
            if (nights < 1) nights = 1;

            return pricePerNight * nights * numPeople;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

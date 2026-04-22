package com.hotelbooking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:hotel.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
    
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_number TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "price_per_night REAL NOT NULL, " +
                "is_available INTEGER NOT NULL DEFAULT 1)");
    
            stmt.execute("CREATE TABLE IF NOT EXISTS guests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT)");
    
            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "guest_id INTEGER, " +
                "room_id INTEGER, " +
                "check_in_date TEXT, " +
                "check_out_date TEXT, " +
                "status TEXT, " +
                "FOREIGN KEY(guest_id) REFERENCES guests(id), " +
                "FOREIGN KEY(room_id) REFERENCES rooms(id))");
    
            stmt.execute("CREATE TABLE IF NOT EXISTS invoices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "booking_id INTEGER, " +
                "total_amount REAL, " +
                "tax REAL, " +
                "grand_total REAL, " +
                "FOREIGN KEY(booking_id) REFERENCES bookings(id))");
    
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)");
    
            // Seed rooms if empty
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms");
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES ('101', 'Single', 1500, 1)");
                stmt.execute("INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES ('102', 'Single', 1500, 1)");
                stmt.execute("INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES ('201', 'Double', 2500, 1)");
                stmt.execute("INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES ('202', 'Double', 2500, 1)");
                stmt.execute("INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES ('301', 'Suite',  5000, 1)");
                stmt.execute("INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES ('302', 'Suite',  5000, 1)");
            }
    
            // Seed users if empty
            var rs2 = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs2.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')");
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('manager', 'manager123', 'MANAGER')");
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('staff', 'staff123', 'STAFF')");
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
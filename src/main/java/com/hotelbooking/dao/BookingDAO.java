package com.hotelbooking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hotelbooking.DatabaseHelper;
import com.hotelbooking.models.Booking;

public class BookingDAO {

    public int addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, booking.getGuestId());
            pstmt.setInt(2, booking.getRoomId());
            pstmt.setString(3, booking.getCheckInDate());
            pstmt.setString(4, booking.getCheckOutDate());
            pstmt.setString(5, "CONFIRMED");
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.name as guest_name, r.room_number " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.id " +
                     "JOIN rooms r ON b.room_id = r.id";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setGuestId(rs.getInt("guest_id"));
                b.setRoomId(rs.getInt("room_id"));
                b.setCheckInDate(rs.getString("check_in_date"));
                b.setCheckOutDate(rs.getString("check_out_date"));
                b.setStatus(rs.getString("status"));
                b.setGuestName(rs.getString("guest_name"));
                b.setRoomNumber(rs.getString("room_number"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getRoomIdByBookingId(int bookingId) {
        String sql = "SELECT room_id FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("room_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void resetAllData() {
        String[] sqls = {
            "DELETE FROM invoices",
            "DELETE FROM bookings", 
            "DELETE FROM guests",
            "UPDATE rooms SET is_available = 1",
            "DELETE FROM sqlite_sequence WHERE name IN ('invoices','bookings','guests')"
        };
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
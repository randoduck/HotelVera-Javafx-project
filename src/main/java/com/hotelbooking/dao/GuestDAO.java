package com.hotelbooking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hotelbooking.DatabaseHelper;
import com.hotelbooking.models.Guest;

public class GuestDAO {

    public int addGuest(Guest guest) {
        String sql = "INSERT INTO guests (name, phone, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guest.getName());
            pstmt.setString(2, guest.getPhone());
            pstmt.setString(3, guest.getEmail());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Guest g = new Guest();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                guests.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }
}
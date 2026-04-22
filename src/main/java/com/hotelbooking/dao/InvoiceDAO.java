package com.hotelbooking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hotelbooking.DatabaseHelper;
import com.hotelbooking.models.Invoice;

public class InvoiceDAO {

    public int createInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (booking_id, total_amount, tax, grand_total) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, invoice.getBookingId());
            pstmt.setDouble(2, invoice.getTotalAmount());
            pstmt.setDouble(3, invoice.getTax());
            pstmt.setDouble(4, invoice.getGrandTotal());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Invoice getInvoiceByBookingId(int bookingId) {
        String sql = "SELECT * FROM invoices WHERE booking_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getInt("id"));
                inv.setBookingId(rs.getInt("booking_id"));
                inv.setTotalAmount(rs.getDouble("total_amount"));
                inv.setTax(rs.getDouble("tax"));
                inv.setGrandTotal(rs.getDouble("grand_total"));
                return inv;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
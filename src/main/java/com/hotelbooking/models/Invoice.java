package com.hotelbooking.models;

public class Invoice {
    private int id;
    private int bookingId;
    private double totalAmount;
    private double tax;
    private double grandTotal;

    public Invoice() {}

    public Invoice(int bookingId, double totalAmount, double tax, double grandTotal) {
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.tax = tax;
        this.grandTotal = grandTotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }
}
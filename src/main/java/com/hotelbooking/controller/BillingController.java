package com.hotelbooking.controller;

import com.hotelbooking.models.Invoice;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BillingController {

    @FXML private Label invoiceIdLabel;
    @FXML private Label bookingIdLabel;
    @FXML private Label guestNameLabel;
    @FXML private Label roomLabel;
    @FXML private Label checkInLabel;
    @FXML private Label checkOutLabel;
    @FXML private Label nightsLabel;
    @FXML private Label roomChargesLabel;
    @FXML private Label taxLabel;
    @FXML private Label grandTotalLabel;

    public void setInvoiceData(Invoice invoice, String guestName, String roomInfo,
                                String checkIn, String checkOut, long nights) {
        invoiceIdLabel.setText(String.valueOf(invoice.getId()));
        bookingIdLabel.setText(String.valueOf(invoice.getBookingId()));
        guestNameLabel.setText(guestName);
        roomLabel.setText(roomInfo);
        checkInLabel.setText(checkIn);
        checkOutLabel.setText(checkOut);
        nightsLabel.setText(nights + " night" + (nights > 1 ? "s" : ""));
        roomChargesLabel.setText("Rs." + String.format("%.2f", invoice.getTotalAmount()));
        taxLabel.setText("Rs." + String.format("%.2f", invoice.getTax()));
        grandTotalLabel.setText("Rs." + String.format("%.2f", invoice.getGrandTotal()));
    }

    @FXML
    private void handleClose() {
        ((Stage) invoiceIdLabel.getScene().getWindow()).close();
    }
}
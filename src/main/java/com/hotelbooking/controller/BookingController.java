package com.hotelbooking.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.hotelbooking.dao.BookingDAO;
import com.hotelbooking.dao.GuestDAO;
import com.hotelbooking.dao.InvoiceDAO;
import com.hotelbooking.dao.RoomDAO;
import com.hotelbooking.models.Booking;
import com.hotelbooking.models.Guest;
import com.hotelbooking.models.Invoice;
import com.hotelbooking.models.Room;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BookingController {

    @FXML private TextField guestNameField;
    @FXML private TextField guestPhoneField;
    @FXML private TextField guestEmailField;
    @FXML private ComboBox<Room> roomComboBox;
    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;

    private RoomDAO roomDAO = new RoomDAO();
    private GuestDAO guestDAO = new GuestDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        // Load only available rooms into the combo box
        List<Room> availableRooms = roomDAO.getAllRooms()
            .stream()
            .filter(Room::isAvailable)
            .toList();
        roomComboBox.getItems().addAll(availableRooms);

        // Auto-calculate price when dates or room changes
        roomComboBox.setOnAction(e -> calculatePrice());
        checkInDate.setOnAction(e -> calculatePrice());
        checkOutDate.setOnAction(e -> calculatePrice());
    }

    private void calculatePrice() {
        Room selectedRoom = roomComboBox.getValue();
        LocalDate inDate = checkInDate.getValue();
        LocalDate outDate = checkOutDate.getValue();

        if (selectedRoom != null && inDate != null && outDate != null
                && outDate.isAfter(inDate)) {
            long nights = ChronoUnit.DAYS.between(inDate, outDate);
            double total = nights * selectedRoom.getPricePerNight();
            totalPriceLabel.setText("₹" + String.format("%.2f", total) +
                " (" + nights + " night" + (nights > 1 ? "s" : "") + ")");
        }
    }

    @FXML
    private void handleBooking() {
        if (guestNameField.getText().isEmpty() || guestPhoneField.getText().isEmpty()
                || roomComboBox.getValue() == null
                || checkInDate.getValue() == null || checkOutDate.getValue() == null) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        LocalDate inDate = checkInDate.getValue();
        LocalDate outDate = checkOutDate.getValue();

        if (!outDate.isAfter(inDate)) {
            statusLabel.setText("Check-out date must be after check-in date.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Save guest
        Guest guest = new Guest();
        guest.setName(guestNameField.getText());
        guest.setPhone(guestPhoneField.getText());
        guest.setEmail(guestEmailField.getText());
        int guestId = guestDAO.addGuest(guest);

        // Save booking
        Room selectedRoom = roomComboBox.getValue();
        Booking booking = new Booking();
        booking.setGuestId(guestId);
        booking.setRoomId(selectedRoom.getId());
        booking.setCheckInDate(inDate.toString());
        booking.setCheckOutDate(outDate.toString());
        booking.setStatus("CONFIRMED");

        int bookingId = bookingDAO.addBooking(booking);
        roomDAO.updateRoomAvailability(selectedRoom.getId(), false);

        if (bookingId != -1) {
            // Calculate billing
            long nights = ChronoUnit.DAYS.between(inDate, outDate);
            double roomCharges = nights * selectedRoom.getPricePerNight();
            double tax = roomCharges * 0.18; // 18% GST
            double grandTotal = roomCharges + tax;

            // Save invoice
            InvoiceDAO invoiceDAO = new InvoiceDAO();
            Invoice invoice = new Invoice(bookingId, roomCharges, tax, grandTotal);
            int invoiceId = invoiceDAO.createInvoice(invoice);
            invoice.setId(invoiceId);

            // Open billing screen
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/hotelbooking/fxml/billing.fxml"));
                Stage billingStage = new Stage();
                billingStage.setTitle("Invoice");
                billingStage.setScene(new Scene(loader.load(), 500, 600));

                BillingController bc = loader.getController();
                bc.setInvoiceData(invoice,
                    guest.getName(),
                    "Room " + selectedRoom.getRoomNumber() + " (" + selectedRoom.getType() + ")",
                    inDate.toString(),
                    outDate.toString(),
                    nights);

                billingStage.show();

                // Close booking window
                ((Stage) guestNameField.getScene().getWindow()).close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) guestNameField.getScene().getWindow()).close();
    }
}
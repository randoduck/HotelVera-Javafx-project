package com.hotelbooking.controller;

import java.io.IOException;
import java.util.List;

import com.hotelbooking.dao.BookingDAO;
import com.hotelbooking.dao.RoomDAO;
import com.hotelbooking.models.Booking;
import com.hotelbooking.models.Room;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label totalRoomsLabel;
    @FXML private Label availableRoomsLabel;
    @FXML private Label occupiedRoomsLabel;
    @FXML private Label totalBookingsLabel;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML private TableColumn<Room, String> roomStatusCol;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdCol;
    @FXML private TableColumn<Booking, String> bookingGuestCol;
    @FXML private TableColumn<Booking, String> bookingRoomCol;
    @FXML private TableColumn<Booking, String> bookingCheckInCol;
    @FXML private TableColumn<Booking, String> bookingCheckOutCol;
    @FXML private TableColumn<Booking, String> bookingStatusCol;

    @FXML private Label cancelStatusLabel;

    private RoomDAO roomDAO = new RoomDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        // Rooms table columns
        roomNumberCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomNumber()));
        roomTypeCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
        roomPriceCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPricePerNight()));
        roomStatusCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().isAvailable() ? "Available" : "Occupied"));

        // Bookings table columns
        bookingIdCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        bookingGuestCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getGuestName()));
        bookingRoomCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomNumber()));
        bookingCheckInCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCheckInDate()));
        bookingCheckOutCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCheckOutDate()));
        bookingStatusCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        loadDashboardData();
    }

    private void loadDashboardData() {
        // Load rooms
        List<Room> rooms = roomDAO.getAllRooms();
        long available = rooms.stream().filter(Room::isAvailable).count();
        long occupied = rooms.stream().filter(r -> !r.isAvailable()).count();

        totalRoomsLabel.setText(String.valueOf(rooms.size()));
        availableRoomsLabel.setText(String.valueOf(available));
        occupiedRoomsLabel.setText(String.valueOf(occupied));
        roomsTable.setItems(FXCollections.observableArrayList(rooms));

        // Load bookings
        List<Booking> bookings = bookingDAO.getAllBookings();
        totalBookingsLabel.setText(String.valueOf(bookings.size()));
        bookingsTable.setItems(FXCollections.observableArrayList(bookings));
    }

    @FXML
    private void handleRefresh() {
        loadDashboardData();
    }

    @FXML
    private void openBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/hotelbooking/fxml/booking.fxml"));
            Stage stage = new Stage();
            stage.setTitle("New Booking");
            stage.setScene(new Scene(loader.load(), 700, 550));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/hotelbooking/fxml/login.fxml"));
            Stage stage = (Stage) roomsTable.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(
                getClass().getResource("/com/hotelbooking/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Hotel Booking System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset All Data");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will delete ALL bookings, guests and invoices. Rooms will be marked available. This cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                bookingDAO.resetAllData();
                loadDashboardData();
                cancelStatusLabel.setText("All data reset successfully.");
                cancelStatusLabel.setStyle("-fx-text-fill: green;");
            }
        });
    }

    @FXML
    private void handleCancelBooking() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            cancelStatusLabel.setText("Please select a booking to cancel.");
            cancelStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (selected.getStatus().equals("CANCELLED")) {
            cancelStatusLabel.setText("This booking is already cancelled.");
            cancelStatusLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        // Confirm dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel booking for " + selected.getGuestName() + "?");
        alert.setContentText("Room " + selected.getRoomNumber() + " will be marked available again.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Cancel booking
                bookingDAO.cancelBooking(selected.getId());

                // Free up the room
                int roomId = bookingDAO.getRoomIdByBookingId(selected.getId());
                if (roomId != -1) {
                    roomDAO.updateRoomAvailability(roomId, true);
                }

                cancelStatusLabel.setText("Booking #" + selected.getId() + " cancelled successfully.");
                cancelStatusLabel.setStyle("-fx-text-fill: green;");

                // Refresh data
                loadDashboardData();
            }
        });
    }
}
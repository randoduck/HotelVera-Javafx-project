package com.hotelbooking.controller;

import java.io.IOException;

import com.hotelbooking.dao.UserDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userDAO.validateUser(username, password)) {
            String role = userDAO.getUserRole(username);
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/hotelbooking/fxml/dashboard.fxml"));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(loader.load(), 900, 600);
                scene.getStylesheets().add(
                    getClass().getResource("/com/hotelbooking/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Hotel Vera — " + username + " (" + role + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
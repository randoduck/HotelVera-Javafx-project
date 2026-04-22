package com.hotelbooking;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseHelper.initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(
            App.class.getResource("/com/hotelbooking/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        // Load CSS globally
        scene.getStylesheets().add(
            getClass().getResource("/com/hotelbooking/css/styles.css").toExternalForm());
        stage.setTitle("Hotel Vera");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

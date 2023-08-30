package com.transferarbeit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    // The main entry point for the JavaFX application.
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }

    // The start method is the main entry point for all JavaFX applications.
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the main GUI view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        Parent root = loader.load();

        // Create a new scene with the loaded root node
        Scene scene = new Scene(root);

        // Set minimum dimensions for the window
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        // Set the title of the application window
        primaryStage.setTitle("Transferarbeit - Kompression");

        // Attach the scene to the primary stage
        primaryStage.setScene(scene);

        // Show the primary stage
        primaryStage.show();
    }
}

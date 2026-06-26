package com.sams;

import com.sams.util.DBConnection;
import com.sams.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application entry point.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        SceneManager.switchScene("/fxml/Login.fxml", "Login");
    }

    @Override
    public void stop() {
        DBConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.sams.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility for switching between FXML screens.
 */
public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Load an FXML file and switch the primary stage scene.
     * @param fxmlPath  e.g. "/fxml/Login.fxml"
     * @param title     Window title
     */
    public static void switchScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                SceneManager.class.getResource("/css/styles.css").toExternalForm()
        );
        primaryStage.setTitle(title + " — SAMS");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Load an FXML and return its controller (useful when you need to pass data).
     */
    public static <T> T switchSceneWithController(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                SceneManager.class.getResource("/css/styles.css").toExternalForm()
        );
        primaryStage.setTitle(title + " — SAMS");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        return loader.getController();
    }
}

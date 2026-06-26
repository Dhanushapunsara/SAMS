package com.sams.controller;

import com.sams.model.User;
import com.sams.service.AuthService;
import com.sams.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * PRESENTATION LAYER — Login screen controller.
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    private void handleLogin() {
        errorLabel.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            boolean ok = authService.login(username, password);
            if (ok) {
                User user = authService.getLoggedInUser();
                if (user.getRole() == User.Role.ADMIN) {
                    SceneManager.switchScene("/fxml/AdminDashboard.fxml", "Admin Dashboard");
                } else {
                    SceneManager.switchScene("/fxml/LecturerDashboard.fxml", "Lecturer Dashboard");
                }
            } else {
                errorLabel.setText("Invalid username or password. Please try again.");
                passwordField.clear();
            }
        } catch (Exception e) {
            errorLabel.setText("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
